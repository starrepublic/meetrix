package com.starrepublic.meetrix.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.starrepublic.meetrix.BR

/**
 * Created by richard on 2016-11-12.
 */
class NewEventDialogViewModel : BaseObservable() {
    @get:Bindable
    var eventName: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.eventName)
        }
    @get:Bindable
    var from: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.from)
        }
    @get:Bindable
    var to: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.to)
        }
}