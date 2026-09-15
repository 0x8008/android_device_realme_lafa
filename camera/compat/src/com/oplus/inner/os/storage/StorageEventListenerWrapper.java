// SPDX-License-Identifier: Apache-2.0
package com.oplus.inner.os.storage;
public class StorageEventListenerWrapper extends android.os.storage.StorageEventListener {
    public void onVolumeStateChanged(VolumeInfoWrapper volume, int oldState, int newState) {}
    @Override public void onVolumeStateChanged(android.os.storage.VolumeInfo volume,
            int oldState, int newState) {
        onVolumeStateChanged(new VolumeInfoWrapper(volume), oldState, newState);
    }
}
