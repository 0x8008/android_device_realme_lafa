package android.os;

import android.view.KeyEvent;

public class OplusKeyEventManager {

    public interface OnKeyEventObserver {
        void onKeyEvent(KeyEvent event); 
    }

    // LAFA API ADAPTATIONS

    private static final OplusKeyEventManager INSTANCE = new OplusKeyEventManager();
    public static OplusKeyEventManager getInstance() { return INSTANCE; }
    // Android delivers this app's keys normally; OEM global-key interception is unavailable.
    public boolean registerKeyEventObserver(android.content.Context context,
            OnKeyEventObserver observer, int flags) { return false; }
    public boolean unregisterKeyEventObserver(android.content.Context context,
            OnKeyEventObserver observer) { return false; }
    public boolean registerKeyEventInterceptor(android.content.Context context, String name,
            OnKeyEventObserver observer, android.util.ArrayMap keys) { return false; }
    public boolean unregisterKeyEventInterceptor(android.content.Context context, String name,
            OnKeyEventObserver observer) { return false; }
}
