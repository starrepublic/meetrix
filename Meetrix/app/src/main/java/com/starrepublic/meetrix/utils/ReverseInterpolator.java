package com.starrepublic.meetrix.utils;

import android.view.animation.Interpolator;

/**
 * Created by richard on 2016-10-30.
 */
public class ReverseInterpolator implements Interpolator {

    private final Interpolator interpolator;

    public ReverseInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public float getInterpolation(float v) {
        return 1-interpolator.getInterpolation(v);
    }
}
