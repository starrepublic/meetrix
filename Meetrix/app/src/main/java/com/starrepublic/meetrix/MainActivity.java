package com.starrepublic.meetrix;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.keyguard.widget.GlowPadView;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.starrepublic.meetrix.databinding.ActivityMainBinding;
import com.starrepublic.meetrix.util.LUtils;
import com.starrepublic.meetrix.util.ReverseInterpolator;
import com.starrepublic.meetrix.util.Utils;
import com.starrepublic.meetrix.widget.EventView;
import com.starrepublic.meetrix.widget.TimeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements ViewTreeObserver.OnScrollChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long RESET_SCROLL_TIME = 30;


    private BroadcastReceiver broadcastReceiver;
    private final SimpleDateFormat sdfWatchTime = new SimpleDateFormat("HH:mm");
    private String time;
    private ActivityMainBinding binding;
    private int timeWidth;
    private int timeHeight;
    private int eventHeight;
    private ValueAnimator newEventWidthAnimator = ValueAnimator.ofInt(0);
    private AlphaAnimation fadeOutAnimation = new AlphaAnimation(1f, 0f);
    private AlphaAnimation fadeInAnimation = new AlphaAnimation(0f, 1f);


    private Interpolator fastOutSlowInInterpolator;
    private Interpolator reverseFastOutSlowInInterpolator;

    private Handler handler = new Handler();
    private Runnable resetTimeRunnable;
    private boolean idle = true;
    private List<TimeView> timeViews = new ArrayList<>(24);
    private Calendar calendar = Calendar.getInstance();
    private int lastHour = -1;
    private int lastScroll = 0;

    private SparseArray<EventView> events = new SparseArray<>(100);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fastOutSlowInInterpolator = LUtils.loadInterpolatorWithFallback(this, android.R.interpolator.fast_out_slow_in, android.R.interpolator.decelerate_cubic);
        reverseFastOutSlowInInterpolator = new ReverseInterpolator(fastOutSlowInInterpolator);

        fadeOutAnimation.setInterpolator(fastOutSlowInInterpolator);
        fadeOutAnimation.setDuration(400);
        fadeOutAnimation.setFillEnabled(true);
        fadeOutAnimation.setFillAfter(true);

        fadeInAnimation.setInterpolator(fastOutSlowInInterpolator);
        fadeInAnimation.setDuration(400);
        fadeInAnimation.setFillEnabled(true);
        fadeInAnimation.setFillAfter(true);

        time = sdfWatchTime.format(new Date());


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.setTime(time);
        binding.setRoom("Nikki Sixx");


        binding.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimeline(true);
            }
        });

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Resources res = getResources();

        timeWidth = res.getDimensionPixelSize(R.dimen.time_width);
        timeHeight = res.getDimensionPixelSize(R.dimen.time_height);
        eventHeight = res.getDimensionPixelSize(R.dimen.event_height);


        newEventWidthAnimator.setDuration(200);
        newEventWidthAnimator.setInterpolator(fastOutSlowInInterpolator);
        newEventWidthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams params = binding.viewNewEvent.getLayoutParams();
                params.width = (int) valueAnimator.getAnimatedValue();
                binding.viewNewEvent.setLayoutParams(params);
            }
        });


        resetTimeRunnable = new Runnable() {
            @Override
            public void run() {
                idle = true;
                resetTimeline(true);

            }
        };


        final int glowPadDiameter = res.getDimensionPixelSize(R.dimen.glowpad_outerring_diameter);

        DisplayMetrics metrics = res.getDisplayMetrics();


        ViewGroup.MarginLayoutParams timeLineParams = (ViewGroup.MarginLayoutParams) binding.viewCurrentTimeline.getLayoutParams();
        timeLineParams.topMargin = -glowPadDiameter / 2 - (Utils.dpToPx(this, (108 / 2f) - 32));


        binding.layoutEvents.setPadding((int) (metrics.widthPixels / 2f), 0, (int) (metrics.widthPixels / 2f), 0);

        binding.glowPad.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {
            }

            @Override
            public void onReleased(View v, int handle) {
                animateNewEvent(binding.viewNewEvent.getWidth(), 0);
            }

            @Override
            public void onTrigger(View v, int target) {
                Log.d(TAG, "triggered:" + target);
            }

            @Override
            public void onGrabbedStateChange(View v, int handle) {
                if (handle == 1) {
                    binding.viewCurrentTimeline.animate().withLayer().translationY(glowPadDiameter / 2).setStartDelay(0).setInterpolator(fastOutSlowInInterpolator).setDuration(200).start();
                } else {
                    binding.viewCurrentTimeline.animate().withLayer().translationY(0).setInterpolator(fastOutSlowInInterpolator).setStartDelay(200).setDuration(200).start();
                }
            }

            @Override
            public void onFinishFinalAnimation() {

            }

            @Override
            public void onTracking(View v, int handle) {
                animateNewEvent(binding.viewNewEvent.getWidth(), 0);
            }

            @Override
            public void onSnapped(View v, int target) {

                //Log.d(TAG, "target:" + target);

                if (target == 3 || target == 9) {
                    target = target == 3 ? 9 : 3;
                }

                int time = 15 + (target / 3 * 15);

                animateNewEvent(binding.viewNewEvent.getWidth(), (int)((time / 60f) * (float)timeWidth));
            }
        });





        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        for (int i = 0; i < 24; i++) {


            TimeView timeView = new TimeView(this);

            calendar.set(Calendar.HOUR_OF_DAY, i);
            timeView.setTime(calendar.getTime());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(timeWidth, timeHeight);
            params.leftMargin = i * timeWidth;
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            binding.layoutEvents.addView(timeView, params);
            timeViews.add(timeView);

        }


        binding.layoutEvents.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resetTimeline(false);
                onScrollChanged();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.layoutEvents.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    binding.layoutEvents.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });


        binding.scrollview.getViewTreeObserver().addOnScrollChangedListener(this);
        binding.scrollview.setSnapTo(timeWidth / 4);


        ViewGroup.MarginLayoutParams newEventParams = (ViewGroup.MarginLayoutParams) binding.viewNewEvent.getLayoutParams();
        newEventParams.leftMargin = (int) (metrics.widthPixels / 2f);

    }

    private void animateNewEvent(int from, int to) {
        newEventWidthAnimator.setIntValues(from,to);
        newEventWidthAnimator.start();
    }

    private void resetTimeline(boolean animate) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        float totalHours = hour + (minute / 60f);

        int targetX = (int) Math.ceil(totalHours * timeWidth);
        if (animate) {
            binding.scrollview.smoothScrollTo(targetX, 0);
        } else {
            binding.scrollview.scrollTo(targetX, 0);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        fetchEvents();
    }

    private void fetchEvents() {

        events.clear();
        for(int i = 0; i < binding.layoutEvents.getChildCount(); i++){
            View child = binding.layoutEvents.getChildAt(i);
            if(child instanceof EventView){
                binding.layoutEvents.removeView(child);
            }
        }
        //binding.layoutEvents.removeAllViews();

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.MINUTE, 0);

        for(int i = 0; i < 12; i++){
            EventView eventView = new EventView(this);
            Event event = new Event();

            cal2.set(Calendar.HOUR_OF_DAY, i*i);

            EventDateTime endTime = new EventDateTime();
            endTime.setDate(new DateTime(new Date(cal2.getTime().getTime()+(1000*60*60))));

            EventDateTime startTime = new EventDateTime();
            startTime.setDate(new DateTime(cal2.getTime()));

            event.setEnd(endTime);
            event.setStart(startTime);
            eventView.setEvent(event);

            cal2.setTime(new Date(event.getStart().getDate().getValue()));



            events.append((int) ((event.getStart().getDate().getValue() % 86400000) / 60 / 1000), eventView);

        }

        for(int i = 0; i < events.size(); i++){

            int key = events.keyAt(i);
            float hour = (float)key/60f;
            // get the object by the key.
            EventView eventView = events.get(key);

            RelativeLayout.LayoutParams eventViewParams = new RelativeLayout.LayoutParams(timeWidth, eventHeight);

            eventViewParams.leftMargin = (int) (hour * timeWidth);
            eventViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            binding.layoutEvents.addView(eventView, eventViewParams);
        }


        //TODO remove this block
        /*if (i % 2 == 0) {
            EventView eventView = new EventView(this);
            RelativeLayout.LayoutParams eventViewParams = new RelativeLayout.LayoutParams(timeWidth, eventHeight);
            eventViewParams.leftMargin = i * timeWidth;
            eventViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
            binding.layoutEvents.addView(eventView, eventViewParams);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    time = sdfWatchTime.format(new Date());
                    binding.setTime(time);

                    if (idle) {
                        resetTimeline(true);
                    }

                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));


    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(resetTimeRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void animateViewTranslationX(View view, float translationX) {
        view.animate().translationX(translationX).setDuration(200).setInterpolator(fastOutSlowInInterpolator).start();
    }

    @Override
    public void onScrollChanged() {
        idle = false;
        int scrollSpeed = Math.abs(binding.scrollview.getScrollX() - lastScroll);

        lastScroll = binding.scrollview.getScrollX();

        float totalHours = binding.scrollview.getScrollX() / (float) (timeWidth);
        int hour = (int) totalHours;
        int minute = (int) Math.floor(totalHours % 1 * 60);


        boolean hourChanged = hour != lastHour;
        int hourDiff = (int) Math.signum(hour - lastHour);
        if (hourChanged) {
            lastHour = hour;
        }

        if (hour == 24) {
            return;
        }

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        TimeView timeView = timeViews.get(hour);
        TextView timeTextView = timeView.getTextView();
        float timeTextViewWidth = timeTextView.getWidth();


        if (hourChanged) {
            timeTextView.setAlpha(0f);
            float translationX = hourDiff == 1 ? 0 : (int) (hourDiff * timeWidth);

            if (scrollSpeed < 20) {
                //noinspection ResourceType
                binding.txtSelectedTime.setTranslationX(translationX);

                if (hourDiff != 1) {
                    animateViewTranslationX(binding.txtSelectedTime, hourDiff * timeTextViewWidth);
                }
            } else {
                binding.txtSelectedTime.setTranslationX(0);
            }

        }

        if (hourChanged && hour > 0) {

            TimeView prevTimeView = timeViews.get(hour - 1);
            prevTimeView.getTextView().clearAnimation();
            prevTimeView.getTextView().setAlpha(1f);
            if (hourDiff == 1) {
                animateViewTranslationX(prevTimeView.getTextView(), 0);
            } else {
                prevTimeView.getTextView().setTranslationX(0);
            }

        }
        if (hourChanged && hour < 23) {
            TimeView nextTimeView = timeViews.get(hour + 1);
            nextTimeView.getTextView().clearAnimation();
            nextTimeView.getTextView().setAlpha(1f);
            nextTimeView.getTextView().setTranslationX(0);
        }


        float offset = timeWidth * (totalHours % 1);

        float diff = timeWidth - timeTextViewWidth;
        float selectedTimeTranslationX = binding.txtSelectedTime.getTranslationX();

        int textOffset = (int) offset;
        int selectedTimeTargetX = 0;
        if (offset > diff) {
            selectedTimeTargetX = (int) -timeTextViewWidth;
            textOffset = (int) diff;
        }
        timeTextView.setTranslationX(textOffset);
        if (!hourChanged && (selectedTimeTranslationX == 0.0 || selectedTimeTranslationX == -timeTextViewWidth) && selectedTimeTargetX != selectedTimeTranslationX) {
            animateViewTranslationX(binding.txtSelectedTime, selectedTimeTargetX);
        }

        binding.txtSelectedTime.setText(TimeView.DATE_FORMAT.format(calendar.getTime()));


        handler.removeCallbacks(resetTimeRunnable);
        handler.postDelayed(resetTimeRunnable, RESET_SCROLL_TIME * 1000);

    }


}
