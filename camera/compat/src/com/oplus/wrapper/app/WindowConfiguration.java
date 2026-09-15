// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.app;
public class WindowConfiguration {
    private final android.app.WindowConfiguration configuration;
    public WindowConfiguration(android.app.WindowConfiguration value) { configuration = value; }
    public android.graphics.Rect getAppBounds() { return configuration.getAppBounds(); }
}
