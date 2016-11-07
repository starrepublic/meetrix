package com.starrepublic.meetrix.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix.MainActivity;
import com.starrepublic.meetrix.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by richard on 2016-10-29.
 */

public class TimeView extends RelativeLayout {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private TextView textView;
    private Date time = new Date();



    public TimeView(Context context) {
        super(context);
        init();
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setTime(Date time) {
        this.time = time;
        textView.setText(DATE_FORMAT.format(time));
    }

    public Date getTime() {
        return time;
    }

    public TextView getTextView() {
        return textView;
    }

    private void init() {

        inflate(getContext(), R.layout.view_time, this);

        textView = ((TextView)findViewById(R.id.txt_time));

    }
}
