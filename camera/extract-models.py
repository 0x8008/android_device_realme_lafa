#!/usr/bin/env python3
"""Restore the matching stock HybridRAW models omitted by the base vendor tree."""
# SPDX-License-Identifier: Apache-2.0
import argparse
import hashlib
import json
from pathlib import Path
import shutil

MODULE = Path(__file__).resolve().parent


def extract(dump):
    records = json.loads((MODULE / 'camera-models.json').read_text())['files']
    # Validate the complete input before changing the module's proprietary files.
    for record in records:
        source = dump / record['source']
        with source.open('rb') as stream:
            digest = hashlib.file_digest(stream, 'sha256').hexdigest()
        if source.stat().st_size != record['size'] or digest != record['sha256']:
            raise ValueError('Stock model checksum mismatch: ' + record['source'])
    supplied = [record for record in records if record['camera_port_supplied']]
    for record in supplied:
        relative = Path(record['installed']).relative_to('odm')
        target = MODULE / 'proprietary' / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(dump / record['source'], target)
        target.chmod(0o644)
    print('Verified', len(records), 'stock HybridRAW models; restored', len(supplied), 'missing models.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('dump', type=Path, help='Extracted matching stock partition tree')
    args = parser.parse_args()
    extract(args.dump)


if __name__ == '__main__':
    main()
