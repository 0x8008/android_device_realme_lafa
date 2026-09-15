// SPDX-License-Identifier: Apache-2.0
package com.oplus.dynamicframerate;
public class AnimationVelocityCalculator {
    private float previousFraction;
    private long previousTime;
    public AnimationVelocityCalculator(android.animation.ValueAnimator animator) {
        previousFraction = animator.getAnimatedFraction();
        previousTime = animator.getCurrentPlayTime();
    }
    public float calculator(int width, android.animation.ValueAnimator animator) {
        long time = animator.getCurrentPlayTime();
        float fraction = animator.getAnimatedFraction();
        float velocity = time > previousTime
                ? Math.abs(width * (fraction - previousFraction) * 1000f / (time - previousTime)) : 0f;
        previousFraction = fraction;
        previousTime = time;
        return velocity;
    }
}
