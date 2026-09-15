// SPDX-License-Identifier: Apache-2.0
package com.oplus.graphics;
public class OplusCanvas {
    private final android.graphics.Canvas canvas;
    public OplusCanvas(android.graphics.Canvas value) { canvas = value; }
    public void drawSmoothRoundRect(android.graphics.RectF rect, float rx, float ry,
            android.graphics.Paint paint, float smoothing) {
        canvas.drawRoundRect(rect, rx, ry, paint);
    }
}
