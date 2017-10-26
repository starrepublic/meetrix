package com.starrepublic.meetrix.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.RelativeLayout

/**
 * Created by richard on 19/05/16.
 */
class InsetsRelativeLayout : RelativeLayout {
    private var zeroInset = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setZeroInset(zeroInset: Boolean) {
        this.zeroInset = zeroInset
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, if (zeroInset) 0 else insets.systemWindowInsetBottom))
        } else {
            return insets
        }
    }
}
