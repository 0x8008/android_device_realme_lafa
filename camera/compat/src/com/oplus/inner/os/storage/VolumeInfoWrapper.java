// SPDX-License-Identifier: Apache-2.0
package com.oplus.inner.os.storage;
public class VolumeInfoWrapper {
    private final android.os.storage.VolumeInfo volume;
    public VolumeInfoWrapper(android.os.storage.VolumeInfo value) { volume = value; }
    public String getId() { return volume.getId(); }
    public String getFsUuid() { return volume.getFsUuid(); }
    public int getType() { return volume.getType(); }
    public int getState() { return volume.getState(); }
    public java.io.File getPath() { return volume.getPath(); }
}
