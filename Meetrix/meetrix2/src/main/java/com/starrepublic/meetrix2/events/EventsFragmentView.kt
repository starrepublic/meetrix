/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package nl.endran.skeleton.fragments

import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Bundle
import android.view.View
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.events.EventsFragment
import com.starrepublic.meetrix2.utils.showSnackBar
import com.starrepublic.meetrix2.utils.showToast
import com.starrepublic.meetrix2.mvp.BaseFragmentView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventsFragmentView @Inject constructor() : BaseFragmentView<EventsFragment, EventsFragmentPresenter.ViewModel, EventsFragmentPresenter>() {


    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
    }



    override fun getViewId() = R.layout.fragment_events

    override fun prepare(rootView: View) {
        //setup listeners


    }







    override fun getViewModel() = object : EventsFragmentPresenter.ViewModel {
        override fun showRooms(it: List<Directory.Resources>?) {

        }

        override fun hasPermission(permission: String): Boolean {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
            fragment
        }

        override fun showEvents(events: List<Event>?) {
            fragment
        }

        override fun showError(error: Throwable?) {

        }

        override fun showToast(message: String) {
            rootView?.showToast(message)
        }

        override fun showSnackbar(message: String) {
            rootView?.showSnackBar(message)
        }
    }
}
