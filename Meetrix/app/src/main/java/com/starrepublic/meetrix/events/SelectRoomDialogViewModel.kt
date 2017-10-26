package com.starrepublic.meetrix.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.starrepublic.meetrix.BR

class SelectRoomDialogViewModel : BaseObservable() {
    @get:Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }
}