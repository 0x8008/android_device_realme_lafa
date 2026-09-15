# Device tree for realme GT 8 Pro (lafa)

Evolution X `cnb` / Android 17 bring-up for the Chinese **RMX5200** variant.
Install this repository at `device/realme/lafa`.

| Item | Configuration |
| --- | --- |
| Codename / platform | `lafa` / Qualcomm SM8850 (`canoe`) |
| Product / OTA assert | `lineage_lafa` / `RE6030L1` |
| Display | 1440 × 3136, density 640 |
| Kernel | Stock prebuilts from `device/realme/lafa-kernel` |
| Stock firmware | `RMX5200_16.0.9.402(CN01)` |
| Kernel / vendor security patch | `2026-07-01` |
| Physical super partition | 18,907,922,432 bytes (`0x467000000`) |

## Build

Follow [BUILDING.md](BUILDING.md) for the pinned dependencies, proprietary-file
extraction and build commands. The [shared source patches](patches/README.md)
are required by this branch's stock HAL selection. The stock kernel is the
validated configuration; the inherited source-kernel option is untested.

## Install and release status

See [FLASHING.md](FLASHING.md) for installation and updates, and
[CHANGELOG.md](CHANGELOG.md) for the September 15 release.

The ROM boots on the tested RMX5200. The integrated realme Camera now saves
correctly colored photos, switches rear lenses, and saves playable standard
video. Aperture remains available. The shareable build retains that tested
camera implementation.

Known camera issues: switching can lag, Dolby Vision recording fails, and
tapping the thumbnail does not open Google Photos. Use Google Photos directly;
for video use Standard format, disable HEVC and adaptive frame rate, and leave
Dolby Vision off. Advanced camera modes and other regional variants are unverified.
Adaptive-brightness and final status-bar changes still need device confirmation.

This branch retains the bring-up `WITH_ADB_INSECURE := true` configuration and
the common tree's development AVB keys/flags. It is an unofficial development
target; production signing and security defaults have not been configured.

## Credits and licensing

Based on the OnePlus SM8850 device work by
[OnePlus-SM8850-Development](https://github.com/OnePlus-SM8850-Development),
[LineageOS](https://github.com/LineageOS) and
[Evolution X](https://github.com/Evolution-X).
Keep the original copyright and SPDX notices when reusing source files.
Proprietary firmware, libraries and kernel prebuilts are obtained separately.
