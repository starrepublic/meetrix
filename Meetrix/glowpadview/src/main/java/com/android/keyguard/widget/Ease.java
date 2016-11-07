package com.android.keyguard.widget;

import android.animation.TimeInterpolator;

class Ease {
    private static final float DOMAIN = 1.0f;
    private static final float DURATION = 1.0f;
    private static final float START = 0.0f;

    static class Linear {
        public static final TimeInterpolator easeNone = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return input;
            }
        };
    }

    static class Cubic {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN*(input/=DURATION)*input*input + START;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN*((input=input/DURATION-1)*input*input + 1) + START;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return ((input/=DURATION/2) < 1.0f) ?
                        (DOMAIN/2*input*input*input + START)
                            : (DOMAIN/2*((input-=2)*input*input + 2) + START);
            }
        };
    }

    static class Quad {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation (float input) {
                return DOMAIN*(input/=DURATION)*input + START;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN *(input/=DURATION)*(input-2) + START;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return ((input/=DURATION/2) < 1) ?
                        (DOMAIN/2*input*input + START)
                            : (-DOMAIN/2 * ((--input)*(input-2) - 1) + START);
            }
        };
    }

    static class Quart {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN*(input/=DURATION)*input*input*input + START;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN * ((input=input/DURATION-1)*input*input*input - 1) + START;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return ((input/=DURATION/2) < 1) ?
                        (DOMAIN/2*input*input*input*input + START)
                            : (-DOMAIN/2 * ((input-=2)*input*input*input - 2) + START);
            }
        };
    }

    static class Quint {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN*(input/=DURATION)*input*input*input*input + START;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN*((input=input/DURATION-1)*input*input*input*input + 1) + START;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return ((input/=DURATION/2) < 1) ?
                        (DOMAIN/2*input*input*input*input*input + START)
                            : (DOMAIN/2*((input-=2)*input*input*input*input + 2) + START);
            }
        };
    }

    static class Sine {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN * (float) Math.cos(input/DURATION * (Math.PI/2)) + DOMAIN + START;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return DOMAIN * (float) Math.sin(input/DURATION * (Math.PI/2)) + START;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float input) {
                return -DOMAIN/2 * ((float)Math.cos(Math.PI*input/DURATION) - 1.0f) + START;
            }
        };
    }

}
