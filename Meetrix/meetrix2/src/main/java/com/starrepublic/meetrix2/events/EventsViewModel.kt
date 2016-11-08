package com.starrepublic.meetrix2.events

import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Bundle
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BR
import nl.endran.skeleton.fragments.EventsFragmentPresenter
import nl.endran.skeleton.fragments.EventsFragmentView
import java.util.*

/**
 * Created by richard on 2016-11-08.
 */
class EventsViewModel : BaseObservable(), EventsFragmentPresenter.ViewModel {

    @get:Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewModel)
        }

    @get:Bindable
    var room: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewModel)
        }


    @get:Bindable
    var currentTime: String = EventsFragmentView.DATE_FORMAT.format(Date())
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewModel)
        }


    override fun showEvents(events: List<Event>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showRooms(it: List<Directory.Resources>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showToast(message: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSnackbar(message: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasPermission(permission: String): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}