// SPDX-License-Identifier: Apache-2.0
package com.oplus.osense.eventinfo;
public class OsenseEventResult {
    private final int eventType, stateType;
    private final android.os.Bundle data;
    public OsenseEventResult(int event, int state, android.os.Bundle extra) {
        eventType = event; stateType = state;
        data = extra == null ? new android.os.Bundle() : new android.os.Bundle(extra);
    }
    public int getEventType() { return eventType; }
    public int getEventStateType() { return stateType; }
    public android.os.Bundle getExtraData() { return new android.os.Bundle(data); }
}
