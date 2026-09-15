#!/usr/bin/env python3
"""Adapt the audited stock APK's capture/storage/preview framework boundaries."""
# SPDX-License-Identifier: Apache-2.0
import argparse
from pathlib import Path
import re


def patch(apk):
    changes = {}

    def change(relative, signature, transform):
        path = apk / relative
        text = path.read_text()
        pattern = r'^\.method [^\n]* ' + re.escape(signature) + r'\n.*?^\.end method'
        matches = list(re.finditer(pattern, text, re.M | re.S))
        if len(matches) != 1:
            raise ValueError('Unexpected stock method: ' + relative + ':' + signature)
        match = matches[0]
        replacement = transform(match.group())
        path.write_text(text[:match.start()] + replacement + text[match.end():])
        changes[relative + ':' + signature] = True

    def config_override(method):
        if 'CaptureCompatibility;' in method:
            raise ValueError('Capture compatibility patch already applied')
        injection = '''
    invoke-static {p0}, Lorg/lineageos/lafacamera/CaptureCompatibility;->configValue(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v0
    if-eqz v0, :lafa_stock_config
    return-object v0
    :lafa_stock_config
'''
        return re.sub(r'(    \.locals [1-9][0-9]*\n)', lambda m: m[0] + injection,
                      method, count=1)

    config = 'smali/com/oplus/camera/configure/CameraConfig.smali'
    change(config, 't(Ljava/lang/String;)Ljava/lang/String;', config_override)
    change(config, 'w(Ljava/lang/String;Z)Ljava/lang/String;', config_override)

    def foreground_capture(method):
        if 'executeDeferJob' not in method:
            raise ValueError('Unexpected deferred-capture capability probe')
        # This is the app's central capability predicate. Its existing false
        # branch selects foreground processing and the normal final-image saver.
        return method.splitlines()[0] + '''
    .locals 1
    const/4 v0, 0x0
    return v0
.end method'''

    change('smali_classes5/com/oplus/camera/feature/defer/a.smali',
           'n(Z[Ljava/lang/String;)Z', foreground_capture)

    # Keep identical argument registers, adding the original receiver as the
    # first static parameter. /range calls remain /range. Custom providers pass
    # through unchanged in the adapter; only MediaStore OEM annotations change.
    counts = {'ContentResolver': 0, 'ContentProviderClient': 0}
    pattern = re.compile(
        r'invoke-virtual(?P<range>/range)? (?P<regs>\{[^}\n]+\}), '
        r'Landroid/content/(?P<type>ContentResolver|ContentProviderClient);->'
        r'(?P<method>insert|update)\((?P<args>Landroid/net/Uri;Landroid/content/ContentValues;[^)\n]*)\)'
        r'(?P<result>Landroid/net/Uri;|I)')
    for path in apk.glob('smali*/**/*.smali'):
        text = path.read_text()

        def adapt(match):
            counts[match['type']] += 1
            return ('invoke-static' + (match['range'] or '') + ' ' + match['regs']
                    + ', Lorg/lineageos/lafacamera/MediaStoreCompatibility;->'
                    + match['method'] + '(Landroid/content/' + match['type'] + ';'
                    + match['args'] + ')' + match['result'])

        updated = pattern.sub(adapt, text)
        if updated != text:
            path.write_text(updated)
    if counts != {'ContentResolver': 37, 'ContentProviderClient': 7}:
        raise ValueError('Unexpected stock MediaStore call sites: ' + str(counts))
    return {'methods': changes, 'storage_call_sites': counts}


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('decoded_apk', type=Path)
    print(patch(parser.parse_args().decoded_apk))
