// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.hardware.camera2;
public class CaptureResult {
    private final android.hardware.camera2.CaptureResult result;
    public CaptureResult(android.hardware.camera2.CaptureResult value) { result = value; }
    public com.oplus.wrapper.hardware.camera2.impl.CameraMetadataNative getNativeMetadata() {
        return new com.oplus.wrapper.hardware.camera2.impl.CameraMetadataNative(result);
    }
}
