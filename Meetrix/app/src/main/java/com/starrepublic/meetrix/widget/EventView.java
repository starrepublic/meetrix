package com.starrepublic.meetrix.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by richard on 2016-10-29.
 */

public class EventView extends LinearLayout {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private Event event;

    public void setEvent(Event event) {
        this.event = event;
        refresh();
    }

    public void refresh(){
        Date now = new Date();
        if(this.event.getEnd().getDate().getValue() < now.getTime()){
            this.setBackgroundColor(0x33000000);
        }else{
            this.setBackgroundColor(0x99000000);
        }
    }


    public EventView(Context context) {
        super(context);
        init();
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }



    private void init() {
        setOrientation(VERTICAL);

        this.setBackgroundColor(0x99000000);

        inflate(getContext(), R.layout.view_event, this);



        int padding = (int) (getResources().getDisplayMetrics().density*8);

        setPadding(padding,padding,padding,padding);

    }


}
