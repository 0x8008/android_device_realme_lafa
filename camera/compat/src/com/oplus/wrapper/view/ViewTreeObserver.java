// SPDX-License-Identifier: Apache-2.0
package com.oplus.wrapper.view;
public class ViewTreeObserver {
    private final android.view.ViewTreeObserver observer;
    private final java.util.Map<OnComputeInternalInsetsListener,
            android.view.ViewTreeObserver.OnComputeInternalInsetsListener> listeners =
            new java.util.IdentityHashMap<>();
    public ViewTreeObserver(android.view.ViewTreeObserver value) { observer = value; }
    public interface OnComputeInternalInsetsListener {
        void onComputeInternalInsets(InternalInsetsInfo info);
    }
    public static class InternalInsetsInfo {
        public static final int TOUCHABLE_INSETS_REGION =
                android.view.ViewTreeObserver.InternalInsetsInfo.TOUCHABLE_INSETS_REGION;
        private final android.view.ViewTreeObserver.InternalInsetsInfo info;
        InternalInsetsInfo(android.view.ViewTreeObserver.InternalInsetsInfo value) { info = value; }
        public android.graphics.Region getTouchableRegion() { return info.touchableRegion; }
        public void setTouchableInsets(int value) { info.setTouchableInsets(value); }
    }
    public void addOnComputeInternalInsetsListener(OnComputeInternalInsetsListener listener) {
        if (listeners.containsKey(listener)) return;
        android.view.ViewTreeObserver.OnComputeInternalInsetsListener bridge =
                info -> listener.onComputeInternalInsets(new InternalInsetsInfo(info));
        listeners.put(listener, bridge);
        observer.addOnComputeInternalInsetsListener(bridge);
    }
    public void removeOnComputeInternalInsetsListener(OnComputeInternalInsetsListener listener) {
        android.view.ViewTreeObserver.OnComputeInternalInsetsListener bridge = listeners.remove(listener);
        if (bridge != null && observer.isAlive()) observer.removeOnComputeInternalInsetsListener(bridge);
    }
}
