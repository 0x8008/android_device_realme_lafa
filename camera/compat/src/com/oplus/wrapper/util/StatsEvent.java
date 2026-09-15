// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.util;
public class StatsEvent {
    // OEM atom schemas are unavailable. Preserve the fluent API without allocating a buffer.
    public static Builder newBuilder() { return new Builder(); }
    public static class Builder {
        public Builder setAtomId(int value) { return this; }
        public Builder writeInt(int value) { return this; }
        public Builder writeLong(long value) { return this; }
        public Builder writeBoolean(boolean value) { return this; }
        public Builder usePooledBuffer() { return this; }
        public StatsEvent build() { return new StatsEvent(); }
    }
}
