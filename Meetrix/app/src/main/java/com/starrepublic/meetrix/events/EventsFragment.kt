package com.starrepublic.meetrix.events

import android.Manifest
import android.accounts.AccountManager
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_POWER_CONNECTED
import android.content.Intent.ACTION_POWER_DISCONNECTED
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.os.*
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ViewDragHelper
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import com.android.keyguard.widget.GlowPadView
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.starrepublic.meetrix.BR
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.databinding.FragmentEventsBinding
import com.starrepublic.meetrix.injections.AppComponent
import com.starrepublic.meetrix.mvp.BaseFragment
import com.starrepublic.meetrix.utils.LUtils
import com.starrepublic.meetrix.utils.ReverseInterpolator
import com.starrepublic.meetrix.utils.dpToPx
import com.starrepublic.meetrix.widget.EventView
import com.starrepublic.meetrix.widget.TimeView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

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

    private var vm: EventsViewModel = EventsViewModel()
    private val receiverTick: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (intent.action.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                updateTime()
                if (idle) {
                    resetTimeline(true)
                }
            }
        }
    }
    private val receiverPower = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val flagScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            if (isCharging) {
                activity?.window?.addFlags(flagScreenOn)
                wakeDevice()
            } else {
                activity?.window?.clearFlags(flagScreenOn)
            }
            val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        }
    }
    private val handler = Handler()
    private var accountPickerShown: Boolean = false
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
    private var selectedTimeInHours: Float = 0F
    private var roomEnabled: Boolean = true
    private val glowPadIds = arrayOf(R.drawable.ic_time_15_selector, R.drawable.ic_time_30_selector, R.drawable.ic_time_45_selector, R.drawable.ic_time_60_selector)
    private var colorAvailable: Int = 0
    private var colorUnavailable: Int = 0
    private lateinit var binding: FragmentEventsBinding
    private lateinit var soundCreate: MediaPlayer
    private lateinit var soundSnap: MediaPlayer
    private lateinit var soundRelease: MediaPlayer
    private lateinit var fullWakeLock: PowerManager.WakeLock

    override fun showRooms(rooms: List<CalendarResource>) {
        val dialog: SelectRoomDialogFragment? = (fragmentManager?.findFragmentByTag("select_room_dialog") as SelectRoomDialogFragment?)
        dialog?.showRooms(rooms)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        val powerManager = context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        fullWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MEETRIX - FULL WAKE LOCK")


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
        val resources = context!!.resources

        context?.let {
            colorAvailable = ContextCompat.getColor(it, R.color.available)
            colorUnavailable = ContextCompat.getColor(it, R.color.unavailable)
        }


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

    override fun pickAccount(intent: Intent) {
        if (!accountPickerShown) {
            accountPickerShown = true
            startActivityForResult(intent, REQUEST_ACCOUNT_PICKER, null)
        }
    }

    override fun requirePermissions(): Array<String> {
        return arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.CHANGE_WIFI_STATE)
    }

    override fun showSelectRoomDialog() {
        val dialog: SelectRoomDialogFragment? = (fragmentManager?.findFragmentByTag("select_room_dialog") as SelectRoomDialogFragment?)
        if (dialog == null) {
            val selectRoomDialogFragment = SelectRoomDialogFragment()
            selectRoomDialogFragment.setTargetFragment(this, 0)
            selectRoomDialogFragment.isCancelable = presenter?.room != null
            selectRoomDialogFragment.show(fragmentManager, "select_room_dialog")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ACCOUNT_PICKER -> {
                accountPickerShown = false
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)

                    presenter?.accountName = accountName
                    presenter?.init()
                }
            }

            presenter?.ERROR_ROOMS -> {
                showSelectRoomDialog()
            }

            presenter?.ERROR_EVENTS -> {
                presenter?.loadEvents()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun wakeDevice() {
        fullWakeLock.acquire()
        val keyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val keyguardLock = keyguardManager.newKeyguardLock("TAG")
        keyguardLock.disableKeyguard()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        timeViews.clear()
        eventViewList.clear()
        roomEnabled = true
        lastHour = -1
        lastBgColor = 0

        binding = DataBindingUtil.inflate<FragmentEventsBinding>(inflater!!, getViewId(), container!!, false)

        binding.setVariable(BR.viewModel, vm)

        binding.txtTime.setOnClickListener { resetTimeline(true) }
        updateTime()
        val dummyEvent = Event()
        dummyEvent.summary = getString(R.string.new_event)
        binding.viewNewEvent.event = dummyEvent

        binding.btnSettings.setOnClickListener {
            showSelectRoomDialog()
        }
        val resources = context!!.resources
        val glowPadDiameter = resources.getDimensionPixelSize(R.dimen.glowpad_outerring_diameter)
        val metrics = resources.displayMetrics
        val timeLineParams = binding.viewCurrentTimeline.layoutParams as ViewGroup.MarginLayoutParams
        timeLineParams.topMargin = -glowPadDiameter / 2 - (context?.dpToPx(108 / 2f - 32) ?: 0)


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
                binding.scrollview.smoothScrollBy(0, 0)
                /*
                 binding.scrollview.scrollStopped()
                val snapTo = (timeWidth / 4f)
                selectedTimeInHours =  (snapTo * Math.round(binding.scrollview.scrollX / snapTo)) / timeWidth.toFloat()*/

                binding.scrollview.scrollable = false
                handler.removeCallbacks(resetTimeRunnable)

                v.playSoundEffect(SoundEffectConstants.CLICK)
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
                soundRelease.start()
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
                soundSnap.start()
                val time = targetToTime(target)
                val event = binding.viewNewEvent.event
                event?.start = EventDateTime().setDateTime(DateTime(selectedTimeToDate()))
                event?.end = EventDateTime().setDateTime(DateTime(selectedTimeToDate().time + time.toLong() * 60L * 1000L))
                binding.viewNewEvent.event = event
                binding.viewNewEvent.textColor = colorAvailable

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
            val timeView = TimeView(context!!)

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
        if (metrics != null) {
            newEventParams.leftMargin = (metrics.widthPixels / 2f).toInt()
        }
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
        //(activity as? EventsActivity)?.toggleImersiveMode(true)
    }

    private fun animateNewEvent(from: Int, to: Int) {
        newEventWidthAnimator.setIntValues(from, to)
        newEventWidthAnimator.start()
    }

    override fun onResume() {
        updateTime()

        soundCreate = MediaPlayer.create(context, R.raw.chime_dim2)
        soundSnap = MediaPlayer.create(context, R.raw.click_04)
        soundRelease = MediaPlayer.create(context, R.raw.pop_char)

        if (isCharging()) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        super.onResume()

        if (fullWakeLock.isHeld) {
            fullWakeLock.release()
        }

        resetTimeout()
    }

    fun isCharging(): Boolean {
        val intent = context?.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
    }

    override fun showError(error: Throwable?, code: Int) {
        error?.printStackTrace()

        if (code == presenter?.ERROR_ROOMS) {
            hideRoomSelectDialog()
        }

        when (error) {
            is GooglePlayServicesAvailabilityIOException -> {
                GooglePlayServicesUtil.showErrorDialogFragment(error.connectionStatusCode, activity, this, 99, {
                    activity?.finish()
                })
            }

            is UserRecoverableAuthIOException -> {
                startActivityForResult(error.intent, code)
            }

            is GoogleJsonResponseException -> {
            }

            else -> {
                showToast(getString(R.string.error))
            }
        }

        if (code == presenter?.ERROR_USERS) {
            presenter?.loadEvents()
        }
    }

    private fun hideRoomSelectDialog() {
        val dialog: SelectRoomDialogFragment? = (fragmentManager?.findFragmentByTag("select_room_dialog") as SelectRoomDialogFragment?)
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


        events.forEach {
            renderEvent(it)
        }

        onScrollChanged()
    }

    private fun renderEvent(event: Event): EventView {
        val eventView = EventView(context!!)
        eventView.event = event
        eventView.textColor = if (roomEnabled) colorAvailable else colorUnavailable

        eventViewList.add(eventView)
        val startHour: Float = eventView.startMinutes / 60f
        val width: Float = (eventView.endMinutes / 60f) - startHour
        val eventViewParams = RelativeLayout.LayoutParams((timeWidth * width).toInt(), eventHeight)
        eventViewParams.leftMargin = (startHour * timeWidth).toInt()
        eventViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1)
        binding.layoutEvents.addView(eventView, eventViewParams)
        return eventView
    }

    override fun createPresenter(appComponent: AppComponent): EventsPresenter? {
        return appComponent.getEventsPresenter()
    }

    override fun getViewId(): Int = R.layout.fragment_events
    override fun onStart() {
        super.onStart()
        val intentFilterPower = IntentFilter()
        intentFilterPower.addAction(ACTION_POWER_CONNECTED)
        intentFilterPower.addAction(ACTION_POWER_DISCONNECTED)

        context?.registerReceiver(receiverTick, IntentFilter(Intent.ACTION_TIME_TICK))
        context?.registerReceiver(receiverPower, intentFilterPower)
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

        soundCreate.release()
        soundRelease.release()
        soundSnap.release()
    }

    override fun onStop() {
        super.onStop()

        if (receiverTick != null) {
            context?.unregisterReceiver(receiverTick)
            context?.unregisterReceiver(receiverPower)
        }
    }

    override fun setRoom(room: CalendarResource) {
        vm.room = room.resourceName
    }

    private fun selectedTimeToHourMinute(): Array<Int> =
            arrayOf(selectedTimeInHours.toInt(), Math.floor((selectedTimeInHours % 1 * 60).toDouble()).toInt())

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


        if (eventBusy != null) {
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
                prevTimeView.textView.translationX = 0f
            }
        }
        if (hourChanged && hour < 23) {
            val nextTimeView = timeViews[hour + 1]
            nextTimeView.textView.clearAnimation()
            nextTimeView.textView.alpha = 1f
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

        binding.txtSelectedTime.text = TimeView.DATE_FORMAT.format(calendar.time)


        resetTimeout()
    }

    private fun resetTimeout() {
        handler.removeCallbacks(resetTimeRunnable)
        handler.postDelayed(resetTimeRunnable, RESET_SCROLL_TIME * 1000L)
    }

    private fun updateBackgroundColor(color: Int) {
        if (color != lastBgColor) {
            eventViewList.forEach {
                it.textColor = color
            }

            backgroundAnimator = ObjectAnimator.ofObject(binding.root, "backgroundColor", ArgbEvaluator(), lastBgColor, color)
            backgroundAnimator?.interpolator = fastOutSlowInInterpolator

            lastBgColor = color

            backgroundAnimator?.start()
        }
    }

    private fun animateViewTranslationX(view: View, translationX: Float, duration: Long = 200) {
        view.animate().withLayer().translationX(translationX).setDuration(duration).setInterpolator(fastOutSlowInInterpolator).start()
    }

    override fun showLoading(message: String?) {
        vm.loading = message
    }

    override fun hideLoading() {
        vm.loading = null
    }

    override fun addEvent(event: Event) {
        soundCreate.start()
        val eventView = renderEvent(event)
        eventView.translationY = eventHeight.toFloat()
        eventView.animate().translationY(0f).setInterpolator(fastOutSlowInInterpolator).setStartDelay(200).setDuration(400).withLayer().start()
        onScrollChanged()
    }

    override fun removeEvent(event: Event) {
        val eventView = eventViewList.find({
            it.event?.id == event.id
        })
        eventView?.animate()?.
                translationY(eventHeight.toFloat())?.
                setInterpolator(fastOutSlowInInterpolator)?.
                setDuration(400)?.
                setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        eventViewList.remove(eventView)
                        binding.layoutEvents.removeView(eventView)
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })?.withLayer()?.start()
    }

    fun dismissedDialog(dialog: DialogFragment) {
        if (dialog is NewEventDialogFragment) {
            resetTimeout()
        }
    }
}
