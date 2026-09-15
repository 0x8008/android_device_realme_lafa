#!/usr/bin/env python3
"""Extract and adapt the RMX5200_16.0.9.402(CN01) stock camera for lafa."""
# SPDX-License-Identifier: Apache-2.0
import argparse
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
import zipfile

MODULE = Path(__file__).resolve().parent
ANDROID_ROOT = MODULE.parents[2]
APK_SHA256 = 'e361723375b4d8def3165668f2745bdf1cad95d4b7ff64b7a735862332cda055'
FRAMEWORK_SHA256 = '0978d8f67a91962942d7c97677fa33013e216f122247115a3b49b86cf8b9f815'
SDK_SHA256 = {
    'com.oplus.camera.unit.sdk': '4e4e04a0e310554993fdb6b8c129c775916917468a4d93d4bf900f0f3d754ad8',
    'com.oplus.camera.unit.sdk.adapter': '402363fd3c98905067793c69bbd8c9eedd86bc1d9315f57cbef5819684a8a6ba',
}


def digest(path):
    with path.open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest()


def run(command):
    result = subprocess.run(list(map(str, command)), capture_output=True, text=True)
    if result.returncode:
        # Tool diagnostics stay with the local extraction invocation.
        sys.stderr.write(result.stdout + result.stderr)
        raise SystemExit(result.returncode)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('dump', type=Path, help='Extracted stock my_product, odm, system, and system_ext partitions')
    parser.add_argument('--apktool', type=Path, help='Apktool JAR; defaults to the Android extract-tools prebuilt')
    parser.add_argument('--patchelf', type=Path, help='Patchelf executable; defaults to the 0.18 extract-tools prebuilt')
    parser.add_argument('--baksmali', default='baksmali')
    parser.add_argument('--smali', default='smali')
    args = parser.parse_args()
    if args.apktool is None or args.patchelf is None:
        sys.path.insert(0, str(ANDROID_ROOT / 'tools/extract-utils'))
        from extract_utils.tools import apktool_path, patchelf_version_path_map
        args.apktool = args.apktool or Path(apktool_path)
        args.patchelf = args.patchelf or Path(patchelf_version_path_map['0_18'])

    def source(relative):
        candidates = [args.dump / relative]
        if relative.startswith('system/'):
            candidates.append(args.dump / 'system' / relative)
        for path in candidates:
            if path.is_file():
                return path
        raise FileNotFoundError('Missing stock file: ' + relative)

    original_apk = source('my_product/app/OplusCamera/OplusCamera.apk')
    if digest(original_apk) != APK_SHA256:
        raise ValueError('Stock camera APK does not match the audited firmware baseline')
    records = json.loads((MODULE / 'blob-provenance.json').read_text())
    output = MODULE / 'proprietary'
    (output / 'framework').mkdir(parents=True, exist_ok=True)
    (output / 'lib64').mkdir(exist_ok=True)
    for entry in records:
        original = source(entry['source'])
        if digest(original) != entry['stock_sha256']:
            raise ValueError('Stock library checksum mismatch: ' + entry['source'])
        target = output / 'lib64' / original.name
        shutil.copy2(original, target)
        if entry['soname_fixed']:
            run([args.patchelf, '--set-soname', original.name, target])

    run([sys.executable, MODULE / 'extract-models.py', args.dump])

    with tempfile.TemporaryDirectory(prefix='lafa-camera-extract-') as temporary:
        work = Path(temporary)
        apk = work / 'apk'
        run(['java', '-Xmx12g', '-jar', args.apktool, 'd', '-f', '-o', apk, original_apk])
        ns = 'http://schemas.android.com/apk/res/android'
        a = '{' + ns + '}'
        ET.register_namespace('android', ns)
        manifest_path = apk / 'AndroidManifest.xml'
        tree = ET.parse(manifest_path)
        manifest = tree.getroot()
        app = manifest.find('application')
        app.set(a + 'extractNativeLibs', 'false')
        libraries = {e.get(a+'name'): e for e in app.findall('uses-library')}
        for name in ['com.oplus.camera.unit.sdk', 'lafa-camera-compat']:
            element = libraries.get(name)
            if element is None:
                element = ET.SubElement(app, 'uses-library', {a+'name': name})
            element.set(a+'required', 'true')
        guard = 'com.oplus.camera.permission.ROM_COMPONENT'
        manifest.insert(0, ET.Element('permission', {a+'name': guard, a+'protectionLevel': 'signature'}))
        for component in app:
            for key in ['permission', 'readPermission', 'writePermission']:
                permission = component.get(a+key)
                if permission and not permission.startswith('android.'):
                    component.set(a+key, guard)
        ET.indent(tree, space='    ')
        tree.write(manifest_path, encoding='utf-8', xml_declaration=True)

        font_fixes = []
        for path in apk.glob('smali*/**/*.smali'):
            text = path.read_text()
            if 'Landroid/content/res/OplusBaseConfiguration;' not in text:
                continue
            def replace(match):
                block = match.group(0)
                header = block.splitlines()[0]
                if ('Landroid/content/res/OplusBaseConfiguration;' not in block
                        or not header.endswith(')Landroid/graphics/Typeface;')):
                    return block
                font_fixes.append(str(path.relative_to(apk)))
                return (header + '\n    .locals 1\n'
                        '    sget-object v0, Landroid/graphics/Typeface;->DEFAULT:Landroid/graphics/Typeface;\n'
                        '    return-object v0\n.end method')
            patched = re.sub(r'^\.method .*?^\.end method', replace, text, flags=re.M | re.S)
            if patched != text:
                path.write_text(patched)
        if len(font_fixes) != 1:
            raise ValueError('Unexpected stock font implementation; review the APK before proceeding')

        # Preserve the stock HEIF JNI declarations and nested options without a stock framework JAR.
        heif = work / 'heif'
        framework = source('system/framework/oplus-framework.jar')
        if digest(framework) != FRAMEWORK_SHA256:
            raise ValueError('Stock framework checksum mismatch')
        with zipfile.ZipFile(framework) as archive:
            for name in archive.namelist():
                if name.endswith('.dex'):
                    dex = work / ('heif-' + name)
                    dex.write_bytes(archive.read(name))
                    run([args.baksmali, 'd', '--classes',
                         'Lcom/oplus/media/OplusHeifWriter;,Lcom/oplus/media/OplusHeifWriter$Options;',
                         '-j', '4', '-o', heif, dex])
        heif_classes = list(heif.rglob('*.smali'))
        if len(heif_classes) != 2:
            raise ValueError('Stock HEIF JNI classes were not found')
        for path in heif_classes:
            target = apk / 'smali_classes35' / path.relative_to(heif)
            target.parent.mkdir(parents=True, exist_ok=True)
            lines = path.read_text().splitlines()
            target.write_text('\n'.join(
                re.sub(r'\b(?:whitelist|greylist(?:-max-[a-z])?|blacklist|test-api)\s*', '', line)
                if line.startswith(('.field ', '.method ')) else line for line in lines) + '\n')

        for sdk, expected in SDK_SHA256.items():
            original = source('my_product/product_overlay/framework/' + sdk + '.jar')
            if digest(original) != expected:
                raise ValueError('Stock SDK checksum mismatch: ' + sdk)
            smali = work / sdk
            with zipfile.ZipFile(original) as archive:
                if len([name for name in archive.namelist() if name.endswith('.dex')]) != 1:
                    raise ValueError('Unexpected SDK DEX layout: ' + sdk)
            run([args.baksmali, 'd', '-j', '8', '-o', smali, original])
            probe = smali / 'com/oplus/camera/facebeauty/OplusFaceBeautyPreview.smali'
            text = probe.read_text()
            before = '/product/lib64/libApsFaceBeautyPreviewProductJni.so'
            if before not in text:
                raise ValueError('Unexpected face-beauty library probe: ' + sdk)
            probe.write_text(text.replace(before, '/system_ext/lib64/libApsFaceBeautyPreviewProductJni.so'))
            dex = work / (sdk + '.dex')
            run([args.smali, 'a', '-a', '36', '-j', '8', '-o', dex, smali])
            with zipfile.ZipFile(original) as zin, zipfile.ZipFile(
                    output / 'framework' / (sdk + '.jar'), 'w', zipfile.ZIP_DEFLATED) as zout:
                for entry in zin.infolist():
                    if not entry.filename.endswith('.dex') and not entry.filename.startswith('META-INF/'):
                        zout.writestr(entry, zin.read(entry))
                zout.writestr('classes.dex', dex.read_bytes())
        run([sys.executable, MODULE / 'patch-capture.py', apk])
        run(['java', '-Xmx12g', '-jar', args.apktool, 'b', apk, '-o', output / 'OplusCamera.apk'])
    print('Extracted stock camera, two adapted SDKs, and', len(records), 'support libraries.')
    print('The ROM build signs the adapted APK with its platform key.')


if __name__ == '__main__':
    main()
