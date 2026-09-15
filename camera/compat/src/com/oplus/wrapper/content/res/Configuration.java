// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.content.res;
public class Configuration {
    private final android.content.res.Configuration configuration;
    public Configuration(android.content.res.Configuration value) { configuration = value; }
    public com.oplus.wrapper.app.WindowConfiguration getWindowConfiguration() {
        return new com.oplus.wrapper.app.WindowConfiguration(configuration.windowConfiguration);
    }
}
