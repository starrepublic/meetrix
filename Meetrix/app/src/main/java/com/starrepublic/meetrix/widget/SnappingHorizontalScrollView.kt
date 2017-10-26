package com.starrepublic.meetrix.widget;

import android.content.Context
import android.hardware.SensorManager
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.MotionEventCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView

/**
 * Created by richard on 2016-10-30.
 */
class SnappingHorizontalScrollView : HorizontalScrollView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        detector = GestureDetectorCompat(context, GestureListener())
    }

    private val detector: GestureDetectorCompat
    private var currentlyTouching: Boolean = false;
    private var currentlyScrolling: Boolean = false
    private var lastDiff: Int = -1;
    var snappingEnabled: Boolean = false;
    var snapTo: Int = 0;
    var scrollable: Boolean = true
    fun scrollStopped() {
        if (snappingEnabled) {
            handler?.post {
                smoothScrollTo(snapTo * Math.round(scrollX / snapTo.toFloat()), 0)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (!scrollable) return false
        return super.onInterceptTouchEvent(ev)
    }

    private var flinging: Boolean = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!scrollable) return false
        this.detector.onTouchEvent(event)
        val action = MotionEventCompat.getActionMasked(event)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                flinging = false
                handler.removeCallbacks(resetRunnable)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!flinging) {
                    scrollStopped()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    private val resetRunnable: Runnable = Runnable { scrollStopped() }

    internal inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val INFLEXION = 0.35f
        private val DECELERATION_RATE = Math.log(0.78) / Math.log(0.9)
        private val ppi: Float
        private val physicalCoeff: Float
        private val flingFriction = ViewConfiguration.getScrollFriction()

        init {
            ppi = context.resources.displayMetrics.density * 160.0f
            physicalCoeff = computeDeceleration(0.84f)
        }

        private fun computeDeceleration(friction: Float): Float {
            return SensorManager.GRAVITY_EARTH * 39.37f * ppi * friction
        }

        private fun getSplineDeceleration(velocity: Float): Double {
            return Math.log((INFLEXION * Math.abs(velocity) / (flingFriction * physicalCoeff)).toDouble())
        }

        override fun onFling(event1: MotionEvent?, event2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            flinging = true
            val l = getSplineDeceleration(velocityX);
            val decelMinusOne = DECELERATION_RATE - 1.0;
            val duration = (1000.0 * Math.exp(l / decelMinusOne)).toLong()
            handler.postDelayed(resetRunnable, duration)
            return false
        }

        override fun onScroll(event1: MotionEvent?, event2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return false
        }
    }
}
