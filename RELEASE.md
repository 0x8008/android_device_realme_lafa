# September 15 release files

Evolution X 12.2 / Android 17 for **realme GT 8 Pro RMX5200 (lafa)**.
The tested camera implementation and default ADB debugging are retained.

Get these files from the release download folder:

| File | Use |
| --- | --- |
| `EvolutionX-17.0-20260915-lafa-12.2-Unofficial.zip` | ROM; Google apps included |
| `recovery-lafa-20260915.img` | Matching recovery for first installation |
| `super-reset-lafa-0x467000000-sparse.img` | First installation only |

Follow [FLASHING.md](FLASHING.md). Existing Evolution X users need only the ROM ZIP.
See [CHANGELOG.md](CHANGELOG.md) for changes and known bugs.

## SHA-256

```text
eda21d6efda75fe51d9ea2689fc6e274915368cb7a0388c3dfcad52444035de9  EvolutionX-17.0-20260915-lafa-12.2-Unofficial.zip
23f6d0e10b7e13b3030bd2a956177e80556afccf98b54d4ec5622af42db2982f  recovery-lafa-20260915.img
45cf51788a3312261f5f89f67a9349642a629f2644b2b061e1f48e0823292793  super-reset-lafa-0x467000000-sparse.img
```

Validation passed for OTA integrity, signing, firmware/update compatibility,
all 173 camera models, unchanged camera binaries and configuration, and
enforcing camera policy.

The ROM uses development signing keys and keeps ADB enabled without host
authorization for debugging. The fresh package still needs a final phone
installation check; its camera binaries and configuration match the tested build.
