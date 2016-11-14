package com.starrepublic.meetrix2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by richard on 2016-10-30.
 */

class SnappingHorizontalScrollView : HorizontalScrollView {



    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {


    }

    private var currentlyTouching:Boolean = false;
    private var currentlyScrolling:Boolean = false
    private var lastDiff:Int = -1;

    var snappingEnabled: Boolean = false;
    var snapTo:Int = 0;

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {

        val diff = Math.abs(x - oldX);
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

        super.onScrollChanged(x, y, oldX, oldY)
    }

    private fun scrollStopped() {
        if(snappingEnabled){
            smoothScrollTo(snapTo*Math.round(getScrollX()/snapTo.toFloat()),0)
        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        if(ev?.action == MotionEvent.ACTION_DOWN){
            currentlyTouching = true;
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {

        when(ev?.action){
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentlyTouching = false;
                if (!currentlyScrolling) {
                    scrollStopped();
                }
            }
        }

        return super.onTouchEvent(ev)
    }
}
