package com.starrepublic.meetrix2.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.widget.ListView
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R

class SelectRoomDialogViewModel() : BaseObservable() {


    @get:Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }
}