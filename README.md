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

## Bring-up status

The September 14, 2026 Evolution X build completed and booted on the RMX5200.
Subsequent `runtimefix1` and `wallpaperfix1` builds passed build, SELinux,
VINTF, OTA integrity and packaged-content checks. Those updates include:

- Recovered portrait status-bar alignment and a separate landscape override.
- Measured super geometry and the observed panel's display configuration.
- Correct default device name and the missing Wi-Fi Display audio dependency.
- Removal of the incompatible Flex clock from the Android 17 wallpaper picker.

Phone confirmation of the latest updates, adaptive-brightness behavior and final
status-bar alignment is pending. Stock realme Camera is not integrated. Other
regional variants and a complete hardware feature matrix remain unverified.
An existing saved device name must be changed manually after an update.

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
