package com.starrepublic.meetrix2.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix2.R

import java.text.SimpleDateFormat;
import java.util.Date;


class EventView : LinearLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setOrientation(VERTICAL);

        this.setBackgroundColor(0x99000000.toInt());

        inflate(getContext(), R.layout.view_event, this);


        val padding: Int = (getResources().getDisplayMetrics().density * 8).toInt();

        setPadding(padding, padding, padding, padding);
    }

    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
    }

    var event: Event? = null
        get() = field
        set(value) {
            field = value
            refresh();
        }

    fun refresh() {
        val now: Date = Date();
        if (this.event?.getEnd()?.getDate()?.getValue()!! < now.getTime()) {
            this.setBackgroundColor(0x33000000);
        } else {
            this.setBackgroundColor(0x99000000.toInt());
        }
    }


}
