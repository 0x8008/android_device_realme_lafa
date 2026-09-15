// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.hardware.camera2.impl;
public class CameraMetadataNative {
    // Retain the owner while native code uses its metadata; do not allocate a temporary copy.
    private final Object owner;
    public CameraMetadataNative(Object value) { owner = value; }
    public long getMetadataPtr() {
        try {
            Object metadata = owner;
            if (owner instanceof android.hardware.camera2.CaptureResult) {
                java.lang.reflect.Field field = android.hardware.camera2.CaptureResult.class
                        .getDeclaredField("mResults");
                field.setAccessible(true);
                metadata = field.get(owner);
            }
            return ((android.hardware.camera2.impl.CameraMetadataNative) metadata).getMetadataPtr();
        } catch (ReflectiveOperationException | ClassCastException exception) {
            android.util.Log.e("LafaCameraMetadata", "Cannot access native capture metadata", exception);
            return 0L;
        }
    }
}
