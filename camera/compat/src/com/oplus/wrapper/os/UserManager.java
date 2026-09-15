// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.os;
public class UserManager {
    private final android.os.UserManager manager;
    public UserManager(android.os.UserManager value) { manager = value; }
    public boolean isGuestUser() { return manager.isGuestUser(); }
}
