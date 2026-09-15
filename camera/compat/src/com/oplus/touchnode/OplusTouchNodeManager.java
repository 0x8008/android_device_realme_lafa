// SPDX-License-Identifier: Apache-2.0
package com.oplus.touchnode;
public class OplusTouchNodeManager {
    private static final OplusTouchNodeManager INSTANCE = new OplusTouchNodeManager();
    public static OplusTouchNodeManager getInstance() { return INSTANCE; }
    // The camera has no direct ownership of touchscreen sysfs nodes.
    public boolean writeNodeFileByDevice(int deviceId, int flag, String value) { return false; }
}
