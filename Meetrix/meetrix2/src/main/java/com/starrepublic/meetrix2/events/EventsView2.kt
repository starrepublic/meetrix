package com.starrepublic.meetrix2.events

import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.mvp.BaseFragment
import com.starrepublic.meetrix2.mvp.BaseFragment2
import com.starrepublic.meetrix2.mvp.BaseViewModel

/**
 * Created by richard on 2016-11-08.
 */
interface EventsView2 : BaseViewModel {

    fun showEvents(events:List<Event>)
}