package com.starrepublic.meetrix.events

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.utils.BroadcastEvents
import com.starrepublic.meetrix.utils.ImmersiveActivity
import com.starrepublic.meetrix.utils.addFragment

/**
 * Created by richard on 2016-11-02.
 */
class EventsActivity : ImmersiveActivity() {
    override fun onResume() {
        super.onResume()
        setFlags()
    }

    private fun setFlags() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setFlags()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_POWER) {
            return true
        }

        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        BroadcastEvents.register(this, messageReceiver, BroadcastEvents.dialogClosedEvent)

        addFragment(android.R.id.content, {
            EventsFragment.newInstance()
        })
    }

    override fun onDestroy() {
        BroadcastEvents.unregister(this, messageReceiver)
        super.onDestroy()
    }
}
