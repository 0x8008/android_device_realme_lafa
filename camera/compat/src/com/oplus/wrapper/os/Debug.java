// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.os;
public class Debug {
    public static String getCallers(int depth) { return android.os.Debug.getCallers(depth); }
}
