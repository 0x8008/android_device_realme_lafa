// SPDX-License-Identifier: Apache-2.0
package com.oplus.inner.os;
public class IHwBinderWrapper {
    public abstract static class DeathRecipientWrapper implements android.os.IHwBinder.DeathRecipient {
        public abstract void serviceDied(long cookie);
    }
}
