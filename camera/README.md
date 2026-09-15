# Stock realme camera for lafa

Integration of `com.oplus.camera` 6.070.172 from
`RMX5200_16.0.9.402(CN01)` into Evolution X Android 17.

## Build integration

Place this source at `vendor/realme/lafa-camera` in the Android tree. Extract
the matching stock `my_product`, `odm`, `system`, and `system_ext` partitions to a
local stock-dump directory. The extractor accepts both `system/framework`
and `system/system/framework` layouts.

The extractor uses Java, Apktool, baksmali, smali, and patchelf. Apktool and
patchelf default to the Android tree's extract-tools prebuilts; `baksmali`
and `smali` must be available as commands or passed explicitly.

```sh
python3 vendor/realme/lafa-camera/extract-camera.py /path/to/stock-dump
git -C hardware/oplus apply ../../vendor/realme/lafa-camera/patches/0001-oplus-camera-ux-hint.patch
git -C vendor/oneplus/sm8850-common apply ../../realme/lafa-camera/patches/0002-select-lafa-sensorbridge.patch
git -C bionic apply ../vendor/realme/lafa-camera/patches/0003-preserve-native-crash-reports.patch
git -C frameworks/native apply ../../vendor/realme/lafa-camera/patches/0004-handle-lafa-p010-planes.patch
```

This device tree already includes the zoom extraction fix and inherits the
product configuration after the common device configuration:

```make
$(call inherit-product, vendor/realme/lafa-camera/camera.mk)
```

Build `lineage_lafa-cp2a-userdebug` with the normal Evolution X build command.
The camera remains alongside Aperture.

## Integration details

- Stock APK, both stock SDKs, and 38 stock support libraries; input checksums
  pin extraction to the inspected firmware. Three library SONAMEs are corrected.
- The APK uses Android's default font and retains protection on OEM components
  through a camera-owned signature permission. It is signed with the ROM's
  platform certificate after modification.
- A camera-specific shared Java library supplies Android adapters and explicit
  fallbacks for unavailable OEM services. It is not a replacement boot framework.
- The stock HEIF JNI classes are extracted selectively. Native capture metadata
  retains its owning capture result while exposing the original pointer.
- SDK library probes match their installation under `system_ext`.
- The model manifest covers all 173 matching stock HybridRAW files. The port
  supplies the 135 omitted by the base vendor tree; the existing 38 remain in
  that tree. Extraction and runtime validation check every model's stock hash.
- `SatSmoothZoom.UseAIDLInput=FALSE` selects the HAL's capture-request zoom
  input. The stock setting bypasses that input in favor of a separate OEM AIDL
  UI channel. The device extraction fix is included in this branch; re-extract `CameraHWConfiguration.config` so the setting is installed.
- Only the main SDK is attached to the app's class loader. The adapter SDK has
  no unique classes and is registered only for stock fallback loading paths.
- The camera has its own SELinux domain using the ROM's platform-app rules and
  camera HAL client attribute. No permissive domain or global neverallow bypass
  is part of this integration.
- The app's native dependency inventory covers 190 processing libraries and
  their dependencies, including absolute-path dynamic loads. Exact missing
  `same_process_hal_file` labels are supplied for app loading with SELinux
  enforcing. Provider-only OEM HAL dependencies are outside this inventory.
- FastRPC also reads the stock `libQnnHtpV81Skel.so` Hexagon payload before
  sending it to the DSP. Its exact ODM path has an app-readable library label;
  the policy verifier checks its unchanged stock hash separately from ARM code.
- The storage-model read during deferred-capture initialization has a dedicated
  label for `/proc/devinfo/ufs` with read access limited to the camera app.
- Camera scratch directories have dedicated labels and shared-object MLS
  attributes so the app and native camera services can use them. Existing
  directories are relabeled during boot after an OTA. The app also receives
  the same unsigned Hexagon and DSP manager HAL client access as Qualcomm
  privileged apps, plus native AIDL wake-lock access for APS processing.
- The startup flag `ro.oplus.camera.defercap.support=1` is retained. The app uses
  its foreground capture path because AOSP MediaProvider does not implement
  Oplus deferred images. Quick visibility is disabled. MediaStore writes omit
  the OEM-only `_camera_quick_uri`, `tagflags`, and `ext_tag_flags` annotations;
  image bytes, standard metadata, and pending/final publication remain intact.
- Preview uses SDR because Oplus SurfaceControl EDR extensions are unavailable.
  This does not disable the photo HDR algorithm. The matching stock
  `libiccprofile.so` is installed for the camera provider's color-profile lookup.
- With `LAFA_CAMERA.enabled`, the native buffer API recognizes Qualcomm's
  P010 VENUS format (`0x7FA30C0A`) as YUV and returns its Y, Cb and Cr planes.
  The camera derives scanlines from the distance between these planes; returning
  a single raw plane leaves Cb null and produces a corrupt row count. The native
  window patch also reports the format's two-byte Y sample stride. Other products
  retain their existing format handling when the camera option is disabled.
- The stock `camera.oemlayer.v2.so` HAL wrapper supplies the OEM camera
  characteristics used by the SDK. Its matching lafa `libsensorbridge.so`
  is selected by the existing shared platform module through `LAFA_CAMERA.enabled`;
  the default platform prebuilt lacks a required symbol. Apply the sensorbridge
  patch after regenerating the shared vendor makefiles.
- The stock `libOplusSecurity.so` decoder is installed under `odm/lib64` and
  labeled for SP-HAL loading, allowing APS and the SDK to read encrypted stock
  configuration without modifying those files.
- APK preoptimization is disabled for this prototype because the stock SDKs use
  dynamic loading and the prebuilt DEX imports do not describe a complete build-time
  class-loader context. Runtime shared-library registrations remain explicit.

## Device status and verification

The tested September 15 camera implementation opens, saves correctly colored
photos, switches rear lenses, and records playable standard video. Select
Standard video format, disable HEVC and adaptive frame rate, and leave Dolby
Vision off. Aperture remains installed.

Known issues: lens switching can lag, Dolby Vision recording fails, and the
thumbnail does not open Google Photos. Open Photos directly. Advanced modes
and OEM cloud services have not been fully validated.

Run the installed-content and enforcing-policy checks after building:

```sh
python3 vendor/realme/lafa-camera/verify-runtime.py out/target/product/lafa
python3 vendor/realme/lafa-camera/verify-policy.py out/target/product/lafa
```

These verify hashes, HAL selection, all 173 models, request-based zoom,
required exports, native/DSP loading permissions, and the camera domain's
continued enforcement. They do not replace testing the resulting build on a phone.

Proprietary outputs are excluded from Git. Do not commit extraction logs,
local stock dumps, credentials, or machine-specific paths. Reference source
provenance is recorded in `compat/REFERENCE.txt` and blob hashes in
`blob-provenance.json`.
