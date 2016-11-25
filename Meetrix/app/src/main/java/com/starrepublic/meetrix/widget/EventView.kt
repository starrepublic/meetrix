package com.starrepublic.meetrix.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView
import com.google.api.client.util.DateTime

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.utils.findViewByIdTyped

import java.text.SimpleDateFormat;
import java.util.*


class EventView : LinearLayout {

    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val CALENDAR: Calendar = Calendar.getInstance()
        val BACKGROUND_COLOR_ENABLED = 0xB3000000
        val BACKGROUND_COLOR_DISABLED = 0x40000000
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
    var expired: Boolean = false

    var event: Event? = null
        get() = field
        set(value) {
            field = value
            startMinutes = toMinutes(value?.start?.dateTime)
            endMinutes = toMinutes(value?.end?.dateTime)
            txtTitle.text = event?.summary
            txtTimeSpan.text = DATE_FORMAT.format(Date(value?.start?.dateTime?.value ?: 0)) + " - "+ DATE_FORMAT.format(Date(value?.end?.dateTime?.value ?: 0))
            txtEventCreator.text = event?.creator?.displayName
            refresh()
        }

    fun refresh() {
        if(event!=null&&event!!.end!=null&&event!!.start!=null) {
            val now: Date = Date()
            if (this.event!!.end.dateTime.value < now.time) {
                this.alpha = 0.6f
                textColor = Color.BLACK
                this.setBackgroundColor(BACKGROUND_COLOR_DISABLED.toInt())
                expired = true
            } else {
                this.setBackgroundColor(BACKGROUND_COLOR_ENABLED.toInt())
                this.alpha = 1f
                expired = false
            }
        }
    }

    var textColor: Int = Color.BLACK
        get() = field
        set(value) {
            var color = Color.BLACK
            if(!expired){
                color = value
            }
            txtTitle.setTextColor(color)
            txtEventCreator.setTextColor(color)
            txtTimeSpan.setTextColor(color)
        }

    init {
        orientation = VERTICAL;
        inflate(context, R.layout.view_event, this);

        txtTitle = findViewByIdTyped<TextView>(R.id.txt_title)
        txtEventCreator = findViewByIdTyped<TextView>(R.id.txt_event_creator)
        txtTimeSpan = findViewByIdTyped<TextView>(R.id.txt_time_span)

        val padding: Int = (resources.displayMetrics.density * 8).toInt();
        setPadding(padding, padding, padding, padding);

        this.setBackgroundColor(BACKGROUND_COLOR_ENABLED.toInt())
    }

    private fun toMinutes(datetime: DateTime?): Float {
        CALENDAR.time = Date(datetime?.value ?: 0)
        return CALENDAR.get(Calendar.MINUTE) + (CALENDAR.get(Calendar.HOUR_OF_DAY) * 60f)
    }




}
