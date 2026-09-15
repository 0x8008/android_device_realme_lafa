// SPDX-License-Identifier: Apache-2.0
package android.bluetooth;
public class OplusBluetoothDevice {
    public OplusBluetoothDevice(BluetoothDevice device) {}
    // OEM accessory classification is unavailable; report no special accessory.
    public int getOplusBluetoothClass() { return 0; }
}
