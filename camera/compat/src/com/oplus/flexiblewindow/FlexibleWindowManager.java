// SPDX-License-Identifier: Apache-2.0
package com.oplus.flexiblewindow;
public class FlexibleWindowManager {
    private static final FlexibleWindowManager INSTANCE = new FlexibleWindowManager();
    public static FlexibleWindowManager getInstance() { return INSTANCE; }
    // No OEM embedded-window service is present on this phone ROM.
    public int getFlexibleWindowState(android.app.Activity activity) { return 0; }
    public void removeEmbeddedContainerTask(int taskId, int containerId) {}
}
