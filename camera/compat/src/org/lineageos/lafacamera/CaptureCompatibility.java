// SPDX-License-Identifier: Apache-2.0
package org.lineageos.lafacamera;

/** Feature choices whose stock implementations require Oplus framework services. */
public final class CaptureCompatibility {
    private CaptureCompatibility() {}

    public static String configValue(String key) {
        if (key == null) return null;
        switch (key) {
            // AOSP MediaProvider has no Oplus deferred-image lifecycle.
            case "com.oplus.camera.feature.capture_defer.support":
            case "com.oplus.camera.feature.capture_defer.quick.visible.support":
            // The port has no Oplus SurfaceControl EDR implementation. Keep
            // preview buffers and their rendering in SDR; photo HDR is separate.
            case "com.oplus.camera.preview.hdr.support":
            case "com.oplus.camera.preview.hdr.video.support":
            case "com.oplus.camera.preview.hdr.front.portrait.support":
            case "com.oplus.camera.preview.hdr.transform.support":
            case "com.oplus.camera.preview.hdr.transform.video.support":
            case "com.oplus.camera.preview.hdr.transform.lut.video.support":
                return "0";
            default:
                return null;
        }
    }
}
