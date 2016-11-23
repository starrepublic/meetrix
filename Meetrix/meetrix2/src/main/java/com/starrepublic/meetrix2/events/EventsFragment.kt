package com.starrepublic.meetrix2.events

import android.Manifest
import android.accounts.AccountManager
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.SparseArray
import android.view.animation.AlphaAnimation
import android.view.animation.Interpolator
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import com.android.keyguard.widget.GlowPadView
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.databinding.DialogSelectRoomBinding
import com.starrepublic.meetrix2.databinding.FragmentEventsBinding
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.mvp.BaseFragment
import com.starrepublic.meetrix2.mvp.BaseViewModel
import com.starrepublic.meetrix2.utils.LUtils
import com.starrepublic.meetrix2.utils.ReverseInterpolator
import com.starrepublic.meetrix2.utils.Utils
import com.starrepublic.meetrix2.utils.dpToPx
import com.starrepublic.meetrix2.widget.TimeView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime
import com.starrepublic.meetrix2.widget.EventView
import android.animation.ObjectAnimator
import android.animation.ArgbEvaluator
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION
import android.os.Build
import android.util.SparseIntArray
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.view.animation.Animation


/**
 * Created by richard on 2016-11-08.
 */
class EventsFragment @Inject constructor() : BaseFragment<EventsView, EventsPresenter>(), EventsView, ViewTreeObserver.OnScrollChangedListener {

    companion object {
        fun newInstance() = EventsFragment()
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val DATE_FORMAT_FULL: SimpleDateFormat = SimpleDateFormat("HH:mm yyyy-MM-dd")
        val RESET_SCROLL_TIME: Int = 15

        val REQUEST_ACCOUNT_PICKER = 1000
    }

    override fun pickAccount(intent: Intent) {
        startActivityForResult(intent, REQUEST_ACCOUNT_PICKER, null)
    }

    private var vm: EventsViewModel = EventsViewModel()
    private var broadcastReceiver: BroadcastReceiver? = null
    private val handler = Handler()
    private var timeWidth: Int = 0
    private var timeHeight: Int = 0
    private var eventHeight: Int = 0
    private val newEventWidthAnimator = ValueAnimator.ofInt(0)
    private val fadeOutAnimation = AlphaAnimation(1f, 0f)
    private val fadeInAnimation = AlphaAnimation(0f, 1f)
    private var backgroundAnimator: ObjectAnimator? = null
    private var lastBgColor: Int = 0


    private var fastOutSlowInInterpolator: Interpolator? = null
    private var reverseFastOutSlowInInterpolator: Interpolator? = null
    private var resetTimeRunnable: Runnable = Runnable {
        idle = true
        resetTimeline(true)
    }
    private var idle = true
    private val timeViews = ArrayList<TimeView>(24)
    private val calendar = Calendar.getInstance()
    private var lastHour = -1
    private var lastScroll = 0
    private val eventViewList = ArrayList<EventView>(100)
    private lateinit var binding: FragmentEventsBinding
    private var selectedTimeInHours: Float = 0F
    private var roomEnabled: Boolean = true
    private val glowPadIds = arrayOf(R.drawable.ic_time_15_selector, R.drawable.ic_time_30_selector, R.drawable.ic_time_45_selector, R.drawable.ic_time_60_selector)

    override fun showRooms(rooms: List<CalendarResource>) {
        val dialog: SelectRoomDialogFragment? = (fragmentManager.findFragmentByTag("select_room_dialog") as SelectRoomDialogFragment?)
        dialog?.showRooms(rooms)
    }


    private var colorAvailable: Int = 0
    private var colorUnavailable: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        fastOutSlowInInterpolator = LUtils.loadInterpolatorWithFallback(context, android.R.interpolator.fast_out_slow_in, android.R.interpolator.decelerate_cubic)
        reverseFastOutSlowInInterpolator = ReverseInterpolator(fastOutSlowInInterpolator)

        fadeOutAnimation.interpolator = fastOutSlowInInterpolator
        fadeOutAnimation.duration = 400
        fadeOutAnimation.isFillEnabled = true
        fadeOutAnimation.fillAfter = true

        fadeInAnimation.interpolator = fastOutSlowInInterpolator
        fadeInAnimation.duration = 400
        fadeInAnimation.isFillEnabled = true
        fadeInAnimation.fillAfter = true


        val resources = context.resources;

        colorAvailable = ContextCompat.getColor(context, R.color.available)
        colorUnavailable = ContextCompat.getColor(context, R.color.unavailable)

        timeWidth = resources.getDimensionPixelSize(R.dimen.time_width)
        timeHeight = resources.getDimensionPixelSize(R.dimen.time_height)
        eventHeight = resources.getDimensionPixelSize(R.dimen.event_height)

        newEventWidthAnimator.duration = 200
        newEventWidthAnimator.interpolator = fastOutSlowInInterpolator
        newEventWidthAnimator.addUpdateListener { valueAnimator ->
            val params = binding.layNewEvent.layoutParams
            params.width = valueAnimator.animatedValue as Int
            binding.layNewEvent.layoutParams = params
        }
    }

    override fun requirePermissions(): Array<String> {
        return arrayOf(Manifest.permission.GET_ACCOUNTS)
    }

    override fun showSelectRoomDialog() {
        val selectRoomDialogFragment = SelectRoomDialogFragment()
        selectRoomDialogFragment.setTargetFragment(this, 0)
        selectRoomDialogFragment.isCancelable = presenter?.room!=null
        selectRoomDialogFragment.show(fragmentManager, "select_room_dialog")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ACCOUNT_PICKER -> if (resultCode === Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)

                presenter?.accountName = accountName
                presenter?.init()
            }
            presenter?.ERROR_ROOMS -> {
                showSelectRoomDialog()
            }
            presenter?.ERROR_EVENTS -> {
                presenter?.loadEvents()
            }
        }
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate<FragmentEventsBinding>(inflater!!, getViewId(), container!!, false);

        binding.setVariable(BR.viewModel, vm);

        binding.txtTime.setOnClickListener { resetTimeline(true) }
        updateTime()

        val dummyEvent = Event()
        dummyEvent.summary = getString(R.string.new_event)
        binding.viewNewEvent.event = dummyEvent
        binding.viewNewEvent.textColor = colorAvailable

        binding.btnSettings.setOnClickListener {
            showSelectRoomDialog()
        }

        val resources = context.resources;

        val glowPadDiameter = resources.getDimensionPixelSize(R.dimen.glowpad_outerring_diameter)

        val metrics = resources.getDisplayMetrics()


        val timeLineParams = binding.viewCurrentTimeline.layoutParams as ViewGroup.MarginLayoutParams
        timeLineParams.topMargin = -glowPadDiameter / 2 - context.dpToPx(108 / 2f - 32)


        binding.layoutEvents.setPadding((metrics.widthPixels / 2f).toInt(), 0, (metrics.widthPixels / 2f).toInt(), 0)

        binding.glowPad?.setOnTriggerListener(object : GlowPadView.OnTriggerListener {


            fun enableViews(glowPad: GlowPadView, index: Int) {

                for (id in glowPad.getTargetIds()) {
                    glowPad.setEnableTarget(id, false)
                }

                for (i in 0..index) {
                    glowPad.setEnableTarget(glowPadIds[i], true)
                }
            }

            override fun onGrabbed(v: View, handle: Int) {

                binding.scrollview.smoothScrollBy(0,0)

                binding.scrollview.scrollable = false

                handler.removeCallbacks(resetTimeRunnable)


                val glowPad = v as GlowPadView
                binding.layNewEvent.visibility = View.VISIBLE



                if (!roomEnabled) {
                    enableViews(glowPad, -1)
                } else {
                    val minutes = Math.round(selectedTimeInHours * 60f)
                    val firstEventMinutes = Math.min(eventViewList.find { it.startMinutes >= minutes }?.startMinutes ?: minutes + 60f, minutes + 60f)
                    val diff = firstEventMinutes - minutes;

                    val index = Math.floor(diff / 15f.toDouble()).toInt()
                    enableViews(glowPad, index - 1)

                }
            }

            override fun onReleased(v: View, handle: Int) {
                animateNewEvent(binding.layNewEvent.width, 0)
                binding.scrollview.scrollable = true
                resetTimeout()
            }

            override fun onTrigger(v: View, target: Int) {
                binding.layNewEvent.visibility = View.GONE
                val time = targetToTime(target)
                showNewEventDialog(time)
            }

            override fun onGrabbedStateChange(v: View, handle: Int) {
                if (handle == 1) {
                    binding.viewCurrentTimeline.animate().withLayer().translationY((glowPadDiameter / 2).toFloat()).setStartDelay(0).setInterpolator(fastOutSlowInInterpolator).setDuration(200).start()
                } else {
                    binding.viewCurrentTimeline.animate().withLayer().translationY(0f).setInterpolator(fastOutSlowInInterpolator).setStartDelay(200).setDuration(200).start()
                }
            }

            override fun onFinishFinalAnimation() {

            }

            override fun onTracking(v: View, handle: Int) {
                animateNewEvent(binding.layNewEvent.width, 0)
            }

            override fun onSnapped(v: View, target: Int) {
                val time = targetToTime(target)

                val event = binding.viewNewEvent.event
                event?.start = EventDateTime().setDateTime(DateTime(selectedTimeToDate()))
                event?.end = EventDateTime().setDateTime(DateTime(selectedTimeToDate().time + time.toLong() * 60L * 1000L))
                binding.viewNewEvent.event = event

                animateNewEvent(binding.layNewEvent.width, (time.toFloat() / 60f * timeWidth.toFloat()).toInt())
            }

            private fun targetToTime(target: Int): Int {
                var theTarget = target

                if (theTarget == 3 || theTarget == 9) {
                    theTarget = if (theTarget == 3) 9 else 3
                }


                return 15 + ((theTarget.toFloat() / 3f) * 15f).toInt()
            }
        })





        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        for (i in 0..23) {


            val timeView = TimeView(context)

            calendar.set(Calendar.HOUR_OF_DAY, i)
            timeView.time = calendar.time

            val params = RelativeLayout.LayoutParams(timeWidth, timeHeight)
            params.leftMargin = i * timeWidth
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
            binding.layoutEvents.addView(timeView, params)
            timeViews.add(timeView)

        }



        binding.layoutEvents.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                resetTimeline(false)
                onScrollChanged()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.layoutEvents.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    binding.layoutEvents.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })


        binding.scrollview.viewTreeObserver.addOnScrollChangedListener(this)
        binding.scrollview.setOnTouchListener { view, motionEvent ->


            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                binding.scrollview.snappingEnabled = true
            }
            false
        }
        binding.scrollview.snapTo = (timeWidth / 4)


        val newEventParams = binding.layNewEvent.layoutParams as ViewGroup.MarginLayoutParams
        newEventParams.leftMargin = (metrics.widthPixels / 2f).toInt()


        val root = binding.root

        return root
    }

    private fun showNewEventDialog(minutes: Int) {


        handler.removeCallbacks(resetTimeRunnable)


        val from = selectedTimeToDate()
        val to = Date(from.time + minutes.toLong() * 60L * 1000L)

        val newEventDialogFragent = NewEventDialogFragment.newInstance(from, to)
        newEventDialogFragent.setTargetFragment(this, 0)
        newEventDialogFragent.show(fragmentManager, "new_event_dialog_fragment")


    }

    private fun animateNewEvent(from: Int, to: Int) {
        newEventWidthAnimator.setIntValues(from, to)
        newEventWidthAnimator.start()
    }

    override fun onResume() {
        super.onResume()


    }


    override fun showError(error: Throwable?, code: Int) {
        error?.printStackTrace()

        when (error) {
            is GooglePlayServicesAvailabilityIOException -> {
                GooglePlayServicesUtil.showErrorDialogFragment(error.connectionStatusCode, activity, this, 99, {
                    activity.finish()
                })
            }
            is UserRecoverableAuthIOException -> {
                if (code == presenter?.ERROR_ROOMS) {
                    hideRoomSelectDialog()
                }
                startActivityForResult(error.intent, code)
            }
            is GoogleJsonResponseException -> {
                if (code == presenter?.ERROR_USERS) {
                    presenter?.loadEvents()
                }
            }
            else -> {
                showToast(getString(R.string.error))
            }
        }
    }

    private fun hideRoomSelectDialog() {
        val dialog: SelectRoomDialogFragment? = (fragmentManager.findFragmentByTag("select_room_dialog") as SelectRoomDialogFragment?)
        dialog?.dismiss()
    }

    override fun getViewModel(): EventsView {
        return this
    }

    override fun showEvents(events: List<Event>) {

        eventViewList.forEach {
            binding.layoutEvents.removeView(it)
        }

        eventViewList.clear()





        events?.forEach {
            renderEvent(it)
        }

        onScrollChanged()

    }

    private fun renderEvent(event: Event) {
        val eventView = EventView(context)
        eventView.event = event
        eventView.textColor = if(roomEnabled) colorAvailable else colorUnavailable  //if(eventView.startMinutes / 60f <= selectedTimeInHours && eventView.endMinutes / 60f > selectedTimeInHours) colorUnavailable else colorAvailable

        eventViewList.add(eventView)
        val startHour: Float = eventView.startMinutes / 60f
        val width: Float = (eventView.endMinutes / 60f) - startHour
        val eventViewParams = RelativeLayout.LayoutParams((timeWidth * width).toInt(), eventHeight)
        eventViewParams.leftMargin = (startHour * timeWidth).toInt()
        eventViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
        binding.layoutEvents.addView(eventView, eventViewParams)

    }


    override fun createPresenter(appComponent: AppComponent): EventsPresenter? {
        return appComponent.getEventsPresenter()
    }

    override fun getViewId(): Int = R.layout.fragment_events


    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    updateTime()
                    if (idle) {
                        resetTimeline(true)
                    }

                }
            }
        }

        activity.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    private fun updateTime() {
        vm.currentTime = DATE_FORMAT.format(Date())
    }

    private fun resetTimeline(animate: Boolean) {

        binding.scrollview.snappingEnabled = false
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val totalHours = hour + minute / 60f


        val targetX = Math.ceil((totalHours * timeWidth).toDouble()).toInt()
        if (animate) {
            binding.scrollview.smoothScrollTo(targetX, 0)
        } else {
            binding.scrollview.scrollTo(targetX, 0)
        }

        eventViewList.forEach(EventView::refresh)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(resetTimeRunnable)
    }

    override fun onStop() {
        super.onStop()
        if (broadcastReceiver != null) {
            activity.unregisterReceiver(broadcastReceiver)
        }
    }

    override fun setRoom(room: CalendarResource) {
        vm.room = room.resourceName
    }


    private fun selectedTimeToHourMinute(): Array<Int> {
        return arrayOf(selectedTimeInHours.toInt(), Math.floor((selectedTimeInHours % 1 * 60).toDouble()).toInt())
    }


    private fun selectedTimeToDate(calendar: Calendar? = null): Date {
        val cal = calendar ?: Calendar.getInstance()
        val hourMinute = selectedTimeToHourMinute()

        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.HOUR_OF_DAY, hourMinute[0])
        cal.set(Calendar.MINUTE, hourMinute[1])

        return cal.time
    }

    override fun onScrollChanged() {

        idle = false
        val scrollSpeed = Math.abs(binding.scrollview.getScrollX() - lastScroll)


        lastScroll = binding.scrollview.getScrollX()

        selectedTimeInHours = binding.scrollview.getScrollX() / timeWidth.toFloat()

        val hourMinute = selectedTimeToHourMinute()

        val hour = hourMinute[0]
        val minute = hourMinute[1]


        val hourChanged = hour != lastHour
        val hourDiff = Math.signum((hour - lastHour).toFloat()).toInt()
        if (hourChanged) {
            lastHour = hour
        }

        if (hour == 24) {
            return
        }


        val eventBusy = eventViewList.find {
            it.startMinutes / 60f <= selectedTimeInHours && it.endMinutes / 60f > selectedTimeInHours
        }


        if (eventBusy!= null) {
            vm.meeting = eventBusy.event?.summary ?: ""
            roomEnabled = false
            updateBackgroundColor(colorUnavailable)
        } else {
            vm.meeting = ""
            roomEnabled = true
            updateBackgroundColor(colorAvailable)
        }

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val timeView = timeViews[hour]
        val timeTextView = timeView.textView
        val timeTextViewWidth = timeTextView.getWidth().toFloat()


        if (hourChanged) {
            timeTextView.setAlpha(0f)
            val translationX = (if (hourDiff == 1) 0 else (hourDiff * timeWidth)).toFloat()

            if (scrollSpeed < 20) {

                binding.txtSelectedTime.setTranslationX(translationX)

                if (hourDiff != 1) {
                    animateViewTranslationX(binding.txtSelectedTime, hourDiff * timeTextViewWidth)
                }
            } else {
                binding.txtSelectedTime.setTranslationX(0f)
            }

        }

        if (hourChanged && hour > 0) {

            val prevTimeView = timeViews[hour - 1]
            prevTimeView.textView.clearAnimation()
            prevTimeView.textView.setAlpha(1f)
            if (hourDiff == 1) {
                animateViewTranslationX(prevTimeView.textView, 0f)
            } else {
                prevTimeView.textView.setTranslationX(0f)
            }

        }
        if (hourChanged && hour < 23) {
            val nextTimeView = timeViews[hour + 1]
            nextTimeView.textView.clearAnimation()
            nextTimeView.textView.setAlpha(1f)
            nextTimeView.textView.setTranslationX(0f)
        }


        val offset = timeWidth * (selectedTimeInHours % 1)

        val diff = timeWidth - timeTextViewWidth
        val selectedTimeTranslationX = binding.txtSelectedTime.getTranslationX()

        var textOffset = offset.toInt()
        var selectedTimeTargetX = 0
        if (offset > diff) {
            selectedTimeTargetX = (-timeTextViewWidth).toInt()
            textOffset = diff.toInt()
            //binding.txtSelectedTime.translationX = -(offset-diff)
        }
        timeTextView.setTranslationX(textOffset.toFloat())
        if (!hourChanged && (selectedTimeTranslationX.toDouble() == 0.0 || selectedTimeTranslationX == -timeTextViewWidth) && selectedTimeTargetX.toFloat() != selectedTimeTranslationX) {
            animateViewTranslationX(binding.txtSelectedTime, selectedTimeTargetX.toFloat())
        }

        binding.txtSelectedTime.setText(TimeView.DATE_FORMAT.format(calendar.time))


        resetTimeout()

    }

    private fun resetTimeout() {
        handler.removeCallbacks(resetTimeRunnable)
        handler.postDelayed(resetTimeRunnable, RESET_SCROLL_TIME * 1000L)
    }

    private fun updateBackgroundColor(color: Int) {
        if (color !== lastBgColor) {

            eventViewList.forEach {
                it.textColor = color
            }

            backgroundAnimator = ObjectAnimator.ofObject(binding.root, "backgroundColor", ArgbEvaluator(), lastBgColor, color)
            backgroundAnimator?.interpolator = fastOutSlowInInterpolator

            lastBgColor = color

            backgroundAnimator?.start()
        }
    }

    private fun animateViewTranslationX(view: View, translationX: Float, duration:Long = 200) {
        view.animate().translationX(translationX).setDuration(duration).setInterpolator(fastOutSlowInInterpolator).start()
    }


    override fun string(messageId: Int): String? {
        when (messageId) {
            presenter?.MESSAGE_USERS -> return getString(R.string.loading_users)
            presenter?.MESSAGE_EVENTS -> return getString(R.string.loading_events)
        }
        return null
    }

    override fun showLoading(message: String?) {
        vm.loading = message
    }

    override fun hideLoading() {
        vm.loading = null
    }

    override fun addEvent(event: Event) {
        renderEvent(event)
        onScrollChanged()
    }

    override fun removeEvent(event: Event) {

    }

    fun dismissedDialog(dialog: DialogFragment) {
        if (dialog is NewEventDialogFragment) {
            resetTimeout()
        }
    }
}
