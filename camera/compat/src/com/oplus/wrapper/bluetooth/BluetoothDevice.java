// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.bluetooth;
public class BluetoothDevice {
    private final android.bluetooth.BluetoothDevice device;
    public BluetoothDevice(android.bluetooth.BluetoothDevice value) { device = value; }
    public boolean isConnected() { return device.isConnected(); }
}
