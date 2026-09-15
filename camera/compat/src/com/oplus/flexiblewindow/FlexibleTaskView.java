package com.oplus.flexiblewindow;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class FlexibleTaskView extends SurfaceView {

    public FlexibleTaskView(Context context) {
        this(context, null);
    }

    public FlexibleTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleTaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FlexibleTaskView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes, true);
    }

    public interface Listener {
        default void onInitialized(boolean isStartSuccess) {}
        default void onReleased() {}
        default void onTaskCreated(int taskId, ComponentName name) {}
        default void onTaskChanged(int taskId, ComponentName name, Rect rect) {}
        default void onTaskVisbilityChanged(int taskId, boolean visible) {}
        default void onTaskRemovalStarted(int taskId) {}
        default void onBackPressedOnTaskRoot(int taskId) {}
        default void updateTouchRegion(Region region) {}
    }

    // LAFA API ADAPTATIONS

    private Listener listener;
    private java.util.concurrent.Executor listenerExecutor;
    public void setListener(java.util.concurrent.Executor executor, Listener value) {
        listenerExecutor = executor;
        listener = value;
        // Tell the caller that an OEM embedded task was not started.
        if (executor != null && value != null) executor.execute(() -> value.onInitialized(false));
    }
    public void release() {
        Listener old = listener;
        java.util.concurrent.Executor executor = listenerExecutor;
        listener = null;
        listenerExecutor = null;
        if (executor != null && old != null) executor.execute(old::onReleased);
    }
    public void resize(Rect rect) {
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null && rect != null) {
            params.width = rect.width(); params.height = rect.height();
            setLayoutParams(params);
        }
    }
}
