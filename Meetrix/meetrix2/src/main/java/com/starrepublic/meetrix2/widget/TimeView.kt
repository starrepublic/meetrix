package com.starrepublic.meetrix2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrepublic.meetrix2.R

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by richard on 2016-10-29.
 */

class TimeView : RelativeLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        inflate(getContext(), R.layout.view_time, this)

        textView = findViewById(R.id.txt_time) as TextView
    }

    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
    }


    var textView:TextView


    var time:Date = Date()
        get() = field
        set(value) {
            field = value
            textView.setText(DATE_FORMAT.format(time));
        }
}
