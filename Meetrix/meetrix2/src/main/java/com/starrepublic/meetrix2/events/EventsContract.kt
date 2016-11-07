package com.starrepublic.meetrix2.events

import android.content.Intent
import android.provider.ContactsContract
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BasePresenter
import com.starrepublic.meetrix2.BaseView

/**
 * Created by richard on 2016-11-02.
 */
interface EventsContract {


    interface View : BaseView<Presenter> {

        fun showEvents(events:List<Event>)
        fun showError(exception: Throwable?)
    }


    interface Presenter : BasePresenter {

        fun result(requestCode: Int, resultCode: Int, data: Intent?)
        fun loadEvents()
        fun addNewEvent()
        fun error(exception: Throwable?)

    }
}