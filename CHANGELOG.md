# September 15, 2026 — Evolution X 12.2 / Android 17

Unofficial build for realme GT 8 Pro **RMX5200 (lafa)**. Google apps included.

## Included changes

- Integrated the stock realme Camera alongside Aperture.
- Fixed camera startup, photo saving, processed photo colors, and rear-lens switching.
- Restored all 173 matching HybridRAW model files and the required camera libraries.
- Adapted photo storage to Android's MediaProvider and corrected Qualcomm P010 plane handling.
- Restored useful native crash reports; camera SELinux policy remains enforcing.
- Retained display/panel configuration, portrait and landscape status-bar changes,
  correct device name, Wi-Fi Display audio dependency, and wallpaper-picker crash fix.
- Kept default ADB debugging and the stock prebuilt kernel.

The camera implementation is unchanged from the version tested successfully on
the phone. A fresh build still requires a final installation check.

## Known issues

- Camera switching can lag.
- Dolby Vision recording does not work.
- Camera thumbnail does not launch Google Photos; open Photos directly.
- Use Standard video format with HEVC and adaptive frame rate disabled.
- Advanced camera modes, adaptive-brightness behavior, and other regional variants
  are not fully validated.

ADB is enabled without host authorization for debugging. This is an unofficial
development build with development signing keys, not a production-secured release.
