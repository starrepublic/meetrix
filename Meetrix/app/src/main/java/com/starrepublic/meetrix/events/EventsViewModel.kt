package com.starrepublic.meetrix.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.starrepublic.meetrix.BR

/**
 * Created by richard on 2016-11-08.
 */
class EventsViewModel : BaseObservable() {
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
    var meeting: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.meeting)
        }
    @get:Bindable
    var currentTime: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentTime)
        }
}
