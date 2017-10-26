package com.starrepublic.meetrix.widget;

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.starrepublic.meetrix.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by richard on 2016-10-29.
 */
class TimeView : RelativeLayout {
    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
    }

    var textView: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        inflate(context, R.layout.view_time, this)
        textView = findViewById(R.id.txt_time)
    }

    var time: Date = Date()
        get() = field
        set(value) {
            field = value
            textView.text = DATE_FORMAT.format(time);
        }
}
