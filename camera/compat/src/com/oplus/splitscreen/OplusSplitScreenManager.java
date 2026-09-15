// SPDX-License-Identifier: Apache-2.0
package com.oplus.splitscreen;
public class OplusSplitScreenManager {
    private static final OplusSplitScreenManager INSTANCE = new OplusSplitScreenManager();
    public static OplusSplitScreenManager getInstance() { return INSTANCE; }
    // No OEM split-screen observer service is installed.
    public boolean unregisterSplitScreenObserver(com.oplus.app.IOplusSplitScreenObserver observer) {
        return false;
    }
}
