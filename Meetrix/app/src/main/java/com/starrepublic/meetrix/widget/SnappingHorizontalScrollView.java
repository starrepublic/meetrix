package com.starrepublic.meetrix.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by richard on 2016-10-30.
 */

public class SnappingHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG = SnappingHorizontalScrollView.class.getSimpleName();
    private boolean currentlyTouching;
    private boolean currentlyScrolling;
    private int lastDiff = -1;

    private int snapTo = 0;


    public SnappingHorizontalScrollView(Context context) {
        super(context);
        init();
    }



    public SnappingHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnappingHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SnappingHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
    }




    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {

        int diff = Math.abs(x - oldX);
        if (diff > 1) {
            currentlyScrolling = true;
            lastDiff = -1;
        } else {
            currentlyScrolling = false;
            if (!currentlyTouching && lastDiff != diff) {
                lastDiff = diff;
                scrollStopped();
            }
        }
        super.onScrollChanged(x, y, oldX, oldY);
    }



    private void scrollStopped() {
        //Log.d(TAG, "SCROLL STOPPED!!!");

        //smoothScrollTo(snapTo*Math.round(getScrollX()/(float)snapTo),0);

    }





    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentlyTouching = true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                currentlyTouching = false;
                if (!currentlyScrolling) {
                    scrollStopped();
                    return super.onTouchEvent(event);
                }
        }
        return super.onTouchEvent(event);
    }

    public int getSnapTo() {
        return snapTo;
    }

    public void setSnapTo(int snapTo) {
        this.snapTo = snapTo;
    }
}
