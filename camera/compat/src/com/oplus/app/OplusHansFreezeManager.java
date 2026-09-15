// SPDX-License-Identifier: Apache-2.0
package com.oplus.app;
public class OplusHansFreezeManager {
    private static final OplusHansFreezeManager INSTANCE = new OplusHansFreezeManager();
    public static OplusHansFreezeManager getInstance() { return INSTANCE; }
    // Cross-app freezing is managed by Android, not by the camera.
    public int requestFastFreeze(android.content.Context context, int timeout, String reason) {
        return -1;
    }
}
