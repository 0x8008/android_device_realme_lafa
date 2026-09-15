// SPDX-License-Identifier: Apache-2.0
package android.os.customize;
public class OplusCustomizeRestrictionManager {
    private final android.app.admin.DevicePolicyManager policy;
    private OplusCustomizeRestrictionManager(android.content.Context context) {
        policy = context.getSystemService(android.app.admin.DevicePolicyManager.class);
    }
    public static OplusCustomizeRestrictionManager getInstance(android.content.Context context) {
        return new OplusCustomizeRestrictionManager(context);
    }
    public boolean getForbidRecordScreenState() {
        return policy != null && policy.getScreenCaptureDisabled(null);
    }
}
