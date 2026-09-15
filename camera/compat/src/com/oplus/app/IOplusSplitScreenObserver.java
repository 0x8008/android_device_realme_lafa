// SPDX-License-Identifier: Apache-2.0
package com.oplus.app;
public interface IOplusSplitScreenObserver extends android.os.IInterface {
    void onStateChanged(String state, android.os.Bundle data) throws android.os.RemoteException;
    abstract class Stub extends android.os.Binder implements IOplusSplitScreenObserver {
        public Stub() { attachInterface(this, "com.oplus.app.IOplusSplitScreenObserver"); }
        public android.os.IBinder asBinder() { return this; }
    }
}
