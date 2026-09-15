#!/usr/bin/env python3
"""Check the packaged SELinux policy against the test2 camera launch failures."""
# SPDX-License-Identifier: Apache-2.0
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile

MODULE = Path(__file__).resolve().parent
CAMERA = 'lafa_stock_camera'
UFS = 'lafa_camera_ufs_proc'
SCRATCH = 'lafa_camera_vendor_scratch_file'
DSP_LIBRARIES = {
    '/odm/lib/rfsa/adsp/libQnnHtpV81Skel.so':
        'cea17e55aec84ab5095d1eb6ff4ab0e85eb873935b858356c777b60874b4f745',
}


def command(*args):
    return subprocess.check_output(list(map(str, args)), text=True).strip()


def partition_tree(product_out, part):
    tree = product_out / part
    if part == 'system' and (tree / 'system/etc').is_dir():
        tree = tree / 'system'
    return tree


def verify(product_out, android_root):
    policy_path = product_out / 'odm/etc/selinux/precompiled_sepolicy'
    if not policy_path.is_file():
        raise ValueError('Missing packaged precompiled SELinux policy')
    # Use Android's policy reader; distribution SETools may reject Android policy.
    sys.path.insert(0, str(android_root / 'system/sepolicy/tests'))
    from policy import Policy
    wrapper = android_root / ('out/soong/.intermediates/system/sepolicy/tests/'
                              'libsepolwrap/linux_glibc_x86_64_shared/libsepolwrap.so')
    policy = Policy(str(policy_path), None, str(wrapper))
    if not any(path == '/devinfo/ufs' and context == 'u:object_r:' + UFS + ':s0'
               for _, path, context, _ in policy.QueryGenfs(fs='proc')):
        raise ValueError('UFS storage information lacks its dedicated proc label')

    def permissions(source, target, tclass):
        result = set()
        for rule in policy.QueryTERule(scontext={source}, tcontext={target}, tclass={tclass}):
            if rule.flavor == 'allow':
                result.update(rule.perms)
        return result

    def require(source, target, tclass, wanted):
        missing = set(wanted.split()) - permissions(source, target, tclass)
        if missing:
            raise ValueError(f'{source} lacks {sorted(missing)} on {target}:{tclass}')

    require(CAMERA, UFS, 'file', 'getattr open read')
    if permissions(CAMERA, UFS, 'file') & {'write', 'append', 'create', 'setattr'}:
        raise ValueError('Camera must only read the storage-information node')
    if permissions(CAMERA, 'proc', 'file') & {'read', 'write', 'open'}:
        raise ValueError('Camera acquired generic proc file access')
    if permissions(CAMERA, 'vendor_data_file', 'dir') & {'write', 'add_name', 'remove_name'}:
        raise ValueError('Camera acquired generic vendor data directory access')
    permissive = command(android_root / 'out/host/linux-x86/bin/sepolicy-analyze',
                         policy_path, 'permissive').split()
    if CAMERA in permissive:
        raise ValueError('Camera domain is permissive')

    inventory = json.loads((MODULE / 'compat/native-loading-inventory.json').read_text())
    libraries = [record['path'] for record in inventory['libraries']]
    for name in libraries:
        path = product_out / name.lstrip('/')
        seen = set()
        while path.is_symlink():
            if path in seen:
                raise ValueError('Native library symlink loop: ' + name)
            seen.add(path)
            target = path.readlink()
            path = product_out / str(target).lstrip('/') if target.is_absolute() else path.parent / target
        if not path.is_file():
            raise ValueError('Missing app native dependency: ' + name)
    scratch_paths = {
        '/data/system/camera_rus': 'lafa_camera_scratch_file',
        '/data/vendor/camera_process': SCRATCH,
        '/data/vendor/camera_process/livephoto': SCRATCH,
        '/data/vendor/camera_process/0': SCRATCH,
        '/data/vendor/cam_alog': SCRATCH,
    }
    # Hexagon payloads are read by FastRPC, not linked into the ARM process.
    # Check them separately from the ARM dependency/symbol inventory.
    for name, digest in DSP_LIBRARIES.items():
        path = product_out / name.lstrip('/')
        if not path.is_file() or hashlib.sha256(path.read_bytes()).hexdigest() != digest:
            raise ValueError('Missing or changed stock camera DSP library: ' + name)
    paths = libraries + list(DSP_LIBRARIES) + list(scratch_paths)
    contexts = '\n'.join(p.read_text()
                         for part in ('system', 'system_ext', 'product', 'vendor', 'odm')
                         for p in (partition_tree(product_out, part) / 'etc/selinux').glob('*_file_contexts'))
    with tempfile.NamedTemporaryFile(mode='w', suffix='-camera-file-contexts') as merged:
        merged.write(contexts)
        merged.flush()
        labels = command('matchpathcon', '-N', '-n', '-f', merged.name, *paths).splitlines()
    if len(labels) != len(paths):
        raise ValueError('Incomplete file-context lookup')
    for path, label in zip(paths, labels):
        expected = scratch_paths.get(path, 'same_process_hal_file')
        if label != 'u:object_r:' + expected + ':s0':
            raise ValueError('Incorrect camera dependency/workspace label: ' + path + ': ' + label)
    require(CAMERA, 'same_process_hal_file', 'file', 'execute read open getattr map')

    models = json.loads((MODULE / 'camera-models.json').read_text())['files']
    model_paths = ['/' + record['installed'] for record in models]
    model_dirs = ['/odm', '/odm/etc', '/odm/etc/camera', '/odm/etc/camera/hybridraw_models']
    with tempfile.NamedTemporaryFile(mode='w', suffix='-model-file-contexts') as merged:
        merged.write(contexts)
        merged.flush()
        model_labels = command('matchpathcon', '-N', '-n', '-f', merged.name,
                               *model_paths).splitlines()
        dir_labels = command('matchpathcon', '-N', '-n', '-f', merged.name,
                             *model_dirs).splitlines()
    if len(model_labels) != len(model_paths) or len(dir_labels) != len(model_dirs):
        raise ValueError('Incomplete HybridRAW model label lookup')
    if any(len(label.split(':')) != 4 for label in model_labels + dir_labels):
        raise ValueError('Missing HybridRAW model or parent-directory file context')
    for label in set(model_labels):
        target = label.split(':')[2]
        for source in (CAMERA, 'hal_camera_server'):
            require(source, target, 'file', 'getattr open read map')
    for label in set(dir_labels):
        target = label.split(':')[2]
        for source in (CAMERA, 'hal_camera_server'):
            require(source, target, 'dir', 'search')

    for target in set(scratch_paths.values()):
        require(CAMERA, target, 'dir', 'read search write add_name create remove_name')
        require(CAMERA, target, 'file', 'read open write create unlink')
        if 'mlstrustedobject' not in policy.QueryTypeAttribute(target, False):
            raise ValueError('Shared workspace is blocked by app MLS categories: ' + target)
        if permissions('untrusted_app', target, 'dir') & {'write', 'add_name'}:
            raise ValueError('Untrusted apps can write the camera workspace')
    require('hal_camera_server', SCRATCH, 'dir', 'search write add_name')
    require('hal_camera_server', SCRATCH, 'file', 'read open write create')
    require(CAMERA, 'vendor_hal_unsignedhexlp_service', 'service_manager', 'find')
    require(CAMERA, 'vendor_hal_dspmanager_aidlservice', 'service_manager', 'find')
    require(CAMERA, 'hal_system_suspend_service', 'service_manager', 'find')
    require(CAMERA, 'system_suspend_server', 'binder', 'call')

    init = (product_out / 'system_ext/etc/init/init.lafa.camera.rc').read_text()
    for path in ('/data/system/camera_rus', '/data/vendor/camera_process', '/data/vendor/cam_alog'):
        if 'restorecon_recursive ' + path not in init:
            raise ValueError('Existing camera workspace will not be relabeled: ' + path)
    return {
        'ufs_read_allowed_with_dedicated_label': True,
        'generic_proc_and_vendor_data_access_not_granted': True,
        'app_native_libraries_verified': len(libraries),
        'stock_dsp_libraries_readable': len(DSP_LIBRARIES),
        'hybridraw_models_readable': len(model_paths),
        'shared_camera_workspace_permissions_and_mls_verified': True,
        'existing_workspace_relabel_on_update': True,
        'unsigned_hexagon_service_access': True,
        'dsp_manager_and_native_wake_lock_access': True,
        'camera_domain_enforcing': True,
        'on_device_tested': False,
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('product_out', type=Path)
    parser.add_argument('--android-root', type=Path, default=Path.cwd(),
                        help='Android checkout with built host policy tools (default: cwd)')
    parser.add_argument('--report', type=Path)
    args = parser.parse_args()
    try:
        result = verify(args.product_out.resolve(), args.android_root.resolve())
    except ValueError as error:
        parser.exit(1, str(error) + '\n')
    text = json.dumps(result, indent=2) + '\n'
    if args.report:
        args.report.write_text(text)
    print(text, end='')


if __name__ == '__main__':
    main()
