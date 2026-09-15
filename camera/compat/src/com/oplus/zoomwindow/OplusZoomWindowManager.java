package com.oplus.zoomwindow;

public class OplusZoomWindowManager {

    public static OplusZoomWindowManager sOplusZoomWindowManager = null;

    public static OplusZoomWindowManager getInstance() {
        if (sOplusZoomWindowManager == null) {
            sOplusZoomWindowManager = new OplusZoomWindowManager();
        }
        return sOplusZoomWindowManager;
    }

    public boolean registerZoomWindowObserver(IOplusZoomWindowObserver observer) {
        return false;
    }

    public boolean unregisterZoomWindowObserver(IOplusZoomWindowObserver observer) {
        return false;
    }

    // LAFA API ADAPTATIONS

    public boolean isSupportZoomMode(String target, int userId, String caller, android.os.Bundle options) {
        return false;
    }
    public int startZoomWindow(android.content.Intent intent, android.os.Bundle options,
            int userId, String caller) {
        android.content.Context context = android.app.ActivityThread.currentApplication();
        if (context == null) return -1;
        try {
            context.startActivity(new android.content.Intent(intent)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK));
            return 0;
        } catch (android.content.ActivityNotFoundException | SecurityException exception) {
            return -1;
        }
    }
}
