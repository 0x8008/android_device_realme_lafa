// SPDX-License-Identifier: Apache-2.0
package com.oplus.os.storage;
public class OplusStorageVolume {
    private final android.os.storage.StorageVolume volume;
    public OplusStorageVolume(android.os.storage.StorageVolume value) { volume = value; }
    public int getOplusReadOnlyType() {
        return android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(volume.getState()) ? 1 : 0;
    }
}
