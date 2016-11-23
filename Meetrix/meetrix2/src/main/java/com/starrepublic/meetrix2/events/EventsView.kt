package com.starrepublic.meetrix2.events

import android.content.Intent
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.mvp.BaseFragment
import com.starrepublic.meetrix2.mvp.BaseViewModel

/**
 * Created by richard on 2016-11-08.
 */
interface EventsView : BaseViewModel {

    fun showEvents(events:List<Event>)
    fun showRooms(rooms: List<CalendarResource>)
    fun setRoom(room: CalendarResource)
    fun pickAccount(intent: Intent)
    fun showSelectRoomDialog()
    fun showLoading(message:String?)
    fun hideLoading()
    fun addEvent(event: Event)
    fun removeEvent(event: Event)
}