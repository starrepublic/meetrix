package com.starrepublic.meetrix2.events

import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Bundle
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BR
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by richard on 2016-11-08.
 */
class EventsViewModel : BaseObservable() {

    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
    }

    @get:Bindable
    var loading: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }

    @get:Bindable
    var room: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.room)
        }


    @get:Bindable
    var currentTime: String = DATE_FORMAT.format(Date())
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentTime)
        }
}
