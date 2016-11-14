package com.starrepublic.meetrix2.events

import android.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.starrepublic.meetrix2.App
import com.starrepublic.meetrix2.utils.addFragmentToActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.starrepublic.meetrix2.utils.BroadcastEvents




/**
 * Created by richard on 2016-11-02.
 */
class EventsActivity : AppCompatActivity() {



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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BroadcastEvents.register(this, messageReceiver, BroadcastEvents.dialogClosedEvent)

        var eventsFragment: EventsFragment? = supportFragmentManager.findFragmentById(R
                .id.content) as EventsFragment?
        if (eventsFragment == null) {
            // Create the fragment
            eventsFragment = EventsFragment.newInstance()

            addFragmentToActivity(eventsFragment, android.R.id.content);
        }

    }

    override fun onDestroy() {
        BroadcastEvents.unregister(this,messageReceiver)
        super.onDestroy()
    }
}