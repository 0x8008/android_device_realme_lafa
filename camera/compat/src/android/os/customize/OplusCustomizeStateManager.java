// SPDX-License-Identifier: Apache-2.0
package android.os.customize;
public class OplusCustomizeStateManager {
    private static final OplusCustomizeStateManager INSTANCE = new OplusCustomizeStateManager();
    public static OplusCustomizeStateManager getInstance(android.content.Context context) {
        return INSTANCE;
    }
}
