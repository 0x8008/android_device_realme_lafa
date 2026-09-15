#!/usr/bin/env python3
"""Check stock camera runtime files and the required startup properties."""
# SPDX-License-Identifier: Apache-2.0
import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import tempfile

MODULE = Path(__file__).resolve().parent
RUNTIME_FILES = {
    'odm/lib64/hw/camera.oemlayer.v2.so': 'HMI',
    'odm/lib64/libOplusSecurity.so': 'decode',
    'odm/lib64/libsensorbridge.so': '_ZN8OemLayer14OSensorManager12sServiceDiedE',
    'vendor/lib64/libiccprofile.so': 'GetICCPayloadForColorSpace',
}
REQUIRED_PROPERTIES = {
    'ro.hardware.camera': 'oemlayer.v2',
    'ro.oplus.camera.defercap.support': '1',
    'ro.oplus.camera.defercap.all.quick.visible.support': '0',
}


def partition_tree(product_out, part):
    tree = product_out / part
    if part == 'system' and (tree / 'system/etc').is_dir():
        tree = tree / 'system'
    return tree


def verify(product_out):
    records = {r['installed']: r for r in json.loads((MODULE / 'blob-provenance.json').read_text())}
    verified = []
    for relative, symbol in RUNTIME_FILES.items():
        path = product_out / relative
        if not path.is_file():
            raise ValueError('Missing runtime library: ' + relative)
        digest = hashlib.sha256(path.read_bytes()).hexdigest()
        if digest != records[relative]['stock_sha256'] or digest != records[relative]['sha256']:
            raise ValueError('Runtime library differs from the matching lafa stock input: ' + relative)
        elf = subprocess.check_output(['readelf', '-W', '-d', '--dyn-syms', str(path)], text=True)
        if not re.search(r'\(SONAME\).*\[' + re.escape(path.name) + r'\]', elf):
            raise ValueError('Incorrect SONAME: ' + relative)
        definitions = [line for line in elf.splitlines()
                       if re.search(r'\b(?:GLOBAL|WEAK)\s+(?:DEFAULT|PROTECTED)\s+(?!UND\b)\S+', line)]
        if not any(line.split()[-1].split('@')[0] == symbol for line in definitions):
            raise ValueError('Required export missing from ' + relative + ': ' + symbol)
        verified.append({'path': relative, 'sha256': digest, 'required_export': symbol})

    properties = {}
    for part in ('system', 'system_ext', 'product', 'vendor', 'odm'):
        for relative in ('build.prop', 'etc/build.prop'):
            path = partition_tree(product_out, part) / relative
            if path.is_file():
                for line in path.read_text().splitlines():
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        properties[key] = value
    for key, expected in REQUIRED_PROPERTIES.items():
        if properties.get(key) != expected:
            raise ValueError('Required camera property missing or incorrect: ' + key + '=' + expected)

    # The decoder is also used by OplusStringJNI from the app's SP-HAL namespace.
    contexts = '\n'.join(p.read_text()
                         for part in ('system', 'system_ext', 'product', 'vendor', 'odm')
                         for p in (partition_tree(product_out, part) / 'etc/selinux').glob('*_file_contexts'))
    with tempfile.NamedTemporaryFile(mode='w', suffix='-camera-file-contexts') as merged:
        merged.write(contexts)
        merged.flush()
        label = subprocess.check_output(['matchpathcon', '-N', '-n', '-f', merged.name,
                                         '/odm/lib64/libOplusSecurity.so'], text=True).strip()
    if label != 'u:object_r:same_process_hal_file:s0':
        raise ValueError('Decoder is not executable from the app SP-HAL namespace: ' + label)

    models = json.loads((MODULE / 'camera-models.json').read_text())['files']
    verified_models = []
    for record in models:
        path = product_out / record['installed']
        if not path.is_file():
            raise ValueError('Missing HybridRAW model: ' + record['installed'])
        with path.open('rb') as stream:
            digest = hashlib.file_digest(stream, 'sha256').hexdigest()
        if path.stat().st_size != record['size'] or digest != record['sha256']:
            raise ValueError('HybridRAW model differs from matching stock: ' + record['installed'])
        verified_models.append({'path': record['installed'], 'sha256': digest})
    configuration = (product_out / 'odm/etc/camera/CameraHWConfiguration.config').read_text()
    section = re.search(r'(?ms)^\[SatSmoothZoom\]\s*\n(.*?)(?=^\[|\Z)', configuration)
    if not section or not re.search(r'(?m)^\s*UseAIDLInput\s*=\s*FALSE\s*$', section[1]):
        raise ValueError('SAT zoom must use capture-request metadata on this port')
    return {'runtime_files': verified, 'camera_hal': 'oemlayer.v2',
            'required_properties': REQUIRED_PROPERTIES,
            'hybridraw_models': verified_models,
            'hybridraw_model_count': len(verified_models),
            'sat_zoom_uses_capture_request_metadata': True,
            'decoder_sphal_label': label, 'device_tested': False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('product_out', type=Path, help='Built or extracted product partition tree')
    parser.add_argument('--report', type=Path)
    args = parser.parse_args()
    try:
        result = verify(args.product_out)
    except ValueError as error:
        parser.exit(1, str(error) + '\n')
    text = json.dumps(result, indent=2) + '\n'
    if args.report:
        args.report.write_text(text)
    print(text, end='')


if __name__ == '__main__':
    main()
