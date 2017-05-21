package com.starrepublic.meetrix.events

import android.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.WindowManager
import com.starrepublic.meetrix.utils.BroadcastEvents
import com.starrepublic.meetrix.utils.addFragment
import com.starrepublic.meetrix.widget.InsetsRelativeLayout
import android.support.v4.view.ViewCompat.onApplyWindowInsets
import android.os.Build
import android.view.WindowInsets
import com.starrepublic.meetrix.utils.ImmersiveActivity


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
        super.onCreate(savedInstanceState)


        BroadcastEvents.register(this, messageReceiver, BroadcastEvents.dialogClosedEvent)

        addFragment(R.id.content, {
            EventsFragment.newInstance()
        })
    }

    override fun onDestroy() {
        BroadcastEvents.unregister(this,messageReceiver)
        super.onDestroy()
    }
}
