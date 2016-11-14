package com.starrepublic.meetrix2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView
import com.google.api.client.util.DateTime

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.utils.findViewByIdTyped

import java.text.SimpleDateFormat;
import java.util.*


class EventView : LinearLayout {

    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val CALENDAR: Calendar = Calendar.getInstance()
        val COLOR_DISABLED = 0x11000000
        val COLOR_ENABLED = 0x33000000
    }


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var  txtTitle: TextView
    private var  txtEventCreator: TextView
    private var  txtTimeSpan: TextView

    var startMinutes: Float = 0F
    var endMinutes: Float = 0F

    var event: Event? = null
        get() = field
        set(value) {
            field = value
            startMinutes = toMinutes(value?.start?.dateTime)
            endMinutes = toMinutes(value?.end?.dateTime)
            txtTitle.text = event?.summary
            txtTimeSpan.text = DATE_FORMAT.format(Date(value?.start?.dateTime?.value ?: 0)) + " - "+ DATE_FORMAT.format(Date(value?.end?.dateTime?.value ?: 0))
            txtEventCreator.text = event?.creator?.displayName
            refresh();
        }

    init {
        orientation = VERTICAL;
        inflate(context, R.layout.view_event, this);

        txtTitle = findViewByIdTyped<TextView>(R.id.txt_title)
        txtEventCreator = findViewByIdTyped<TextView>(R.id.txt_event_creator)
        txtTimeSpan = findViewByIdTyped<TextView>(R.id.txt_time_span)

        val padding: Int = (resources.displayMetrics.density * 8).toInt();
        setPadding(padding, padding, padding, padding);

        this.setBackgroundColor(COLOR_ENABLED)
    }

    private fun toMinutes(datetime: DateTime?): Float {
        CALENDAR.time = Date(datetime?.value ?: 0)
        return CALENDAR.get(Calendar.MINUTE) + (CALENDAR.get(Calendar.HOUR_OF_DAY) * 60f)
    }

    fun refresh() {
        if(event!=null&&event!!.end!=null&&event!!.start!=null) {
            val now: Date = Date()
            if (this.event!!.end.dateTime.value < now.time) {
                this.alpha = 0.4f
            } else {
                this.alpha = 1f
            }
        }
    }


}
