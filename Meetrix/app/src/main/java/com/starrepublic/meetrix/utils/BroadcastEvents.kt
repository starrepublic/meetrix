package com.starrepublic.meetrix.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

/**
 * Created by richard on 2016-11-12.
 */
class BroadcastEvents {

    companion object{
        val dialogClosedEvent = "dialog_closed"
        fun register(context:Context, messageReceiver: BroadcastReceiver, filter: String) {
            LocalBroadcastManager.getInstance(context).registerReceiver(messageReceiver,
                    IntentFilter(filter));
        }

        fun unregister(context: Context, messageReceiver: BroadcastReceiver) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(messageReceiver)
        }

        fun send(context: Context?, intent: Intent) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}