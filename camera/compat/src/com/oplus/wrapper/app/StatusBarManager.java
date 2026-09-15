// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.app;
public class StatusBarManager {
    private final android.app.StatusBarManager manager;
    public StatusBarManager(android.app.StatusBarManager value) { manager = value; }
    public void collapsePanels() { manager.collapsePanels(); }
    public void expandNotificationsPanel() { manager.expandNotificationsPanel(); }
}
