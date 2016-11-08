package com.starrepublic.meetrix2.events

import android.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.starrepublic.meetrix2.App
import com.starrepublic.meetrix2.utils.addFragmentToActivity


/**
 * Created by richard on 2016-11-02.
 */
class EventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var eventsFragment: EventsFragment? = supportFragmentManager.findFragmentById(R
                .id.content) as EventsFragment?
        if (eventsFragment == null) {
            // Create the fragment
            eventsFragment = EventsFragment.newInstance()

            addFragmentToActivity(eventsFragment, android.R.id.content);
        }

    }
}