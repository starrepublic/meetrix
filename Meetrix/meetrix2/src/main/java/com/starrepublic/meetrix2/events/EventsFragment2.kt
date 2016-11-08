package com.starrepublic.meetrix2.events

import android.view.View
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.mvp.BaseFragment2
import com.starrepublic.meetrix2.mvp.BaseViewModel

/**
 * Created by richard on 2016-11-08.
 */
class EventsFragment2 : BaseFragment2<EventsView2, EventsPresenter2>(), EventsView2{


    override fun getViewModel(): EventsView2 {
        return this
    }

    override fun showEvents(events: List<Event>) {

    }


    override fun createPresenter(appComponent: AppComponent): EventsPresenter2? {
        return appComponent.getEventsPresenter2()
    }

    override fun getViewId(): Int = R.layout.fragment_events

}
