# Building Evolution X for lafa

## Source setup

Use a Linux Android build host with the Evolution X build prerequisites,
`repo`, Git LFS, curl and Python 3 installed. Run these commands in Bash from the
Android source root:

```bash
repo init -u https://github.com/Evolution-X/manifest -b cnb --git-lfs
mkdir -p .repo/local_manifests
curl -fL https://raw.githubusercontent.com/0x8008/android_device_realme_lafa/evox-cnb/manifests/lafa.xml -o .repo/local_manifests/lafa.xml
repo sync -c -j8
git -C device/realme/lafa-kernel lfs install --local
git -C device/realme/lafa-kernel lfs pull
git -C vendor/oneplus/sm8850-common lfs install --local
git -C vendor/oneplus/sm8850-common lfs pull
```

For an existing checkout, install the local manifest before syncing. Resolve
any duplicate local-manifest entries for the same project paths first.
`lineage.dependencies` records the device dependencies for Evolution X's
dependency resolver; the local manifest additionally pins the patched HALs,
extract-utils, Qualcomm vendor policy and clock package configuration.

The tested manifest repository revision was
`a649e1e4da87120c909bd4991188747c64fb81e3` on September 14, 2026. This local
manifest pins the bring-up dependencies, not every Android project. Future
changes on `cnb` may require a new compatibility review.

## Shared source changes

Copy the included camera integration into its build location:

```bash
mkdir -p vendor/realme/lafa-camera
cp -a device/realme/lafa/camera/. vendor/realme/lafa-camera/
```

Apply [patches/README.md](patches/README.md), including both upstream audio
dependency commits and the camera patches, before building or regenerating common vendor rules.
The common vendor tree retains its tested OnePlus firmware baseline while
device-specific files come from the realme OTA below.

## Extract proprietary files

Use the full stock **RMX5200_16.0.9.402(CN01)** OTA:

| Field | Value |
| --- | --- |
| Original filename | `f434b3e1f96644ea9b31823ae3ca064a.zip` |
| Size | 10,256,324,817 bytes |
| SHA-256 | `7d73cd4100e6d28b3493376909a0efdb68bf89fc619f63b5b5b41c3ff090d2b8` |
| Runtime fingerprint | `realme/RMX5200/RE6030L1:16/BP2A.250605.015/B.202607180226:user/release-keys` |

Substitute the absolute path to that OTA:

```bash
sha256sum /absolute/path/to/ota.zip
(
    cd device/realme/lafa
    ./extract-files.py --only-target /absolute/path/to/ota.zip
)
```

Check the digest against the table before extraction. `--only-target` generates
`vendor/realme/lafa` and preserves the separately synced common vendor tree.
Do not extract the OnePlus common file list from the realme OTA. The validated
extraction contained all 1,743 device entries and 40 firmware images.

For an already extracted vendor tree, regenerate its build rules with:

```bash
(
    cd device/realme/lafa
    ./setup-makefiles.py --only-target
)
```

Extract the stock camera using the same firmware's unpacked `my_product`,
`odm`, `system`, and `system_ext` partition trees:

```bash
python3 vendor/realme/lafa-camera/extract-camera.py /absolute/path/to/stock-dump
```

The extractor verifies pinned input hashes and creates the APK, SDKs, libraries,
and missing HybridRAW models. Java, `baksmali`, and `smali` must be installed;
Apktool and patchelf use the synced extract-tools prebuilts. See
[camera/README.md](camera/README.md). The zoom extraction fix is already part of
this device tree; regenerate the device blobs when updating an older checkout.

## Compile

```bash
source build/envsetup.sh
lunch lineage_lafa-cp2a-userdebug
export BUILD_USERNAME=lafa BUILD_HOSTNAME=android-build
ulimit -n 65536
m -j4 target-files-package
out/host/linux-x86/bin/ota_from_target_files \
    --path out/host/linux-x86 --backup=true --max_threads 2 \
    -k build/make/target/product/security/testkey \
    out/target/product/lafa/obj/PACKAGING/target_files_intermediates/lineage_lafa-target_files \
    out/target/product/lafa/EvolutionX-lafa.zip
```

The validated builds use the default GMS configuration and the prebuilt kernel.
Image compilation and OTA packaging have separate limits to bound memory use.
Default ADB remains enabled as requested for debugging. The neutral build identity
keeps host account and machine names out of generated release metadata.

The OTA is written under `out/target/product/lafa/`. A successful build checks
SELinux, VINTF compatibility and partition sizes. Validate the resulting OTA
and test it on the intended phone before treating a new build as a release.
The [bring-up status](README.md#bring-up-status) records the current test limits.
