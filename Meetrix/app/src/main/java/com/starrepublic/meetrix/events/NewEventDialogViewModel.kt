package com.starrepublic.meetrix.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.starrepublic.meetrix.BR
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.databinding.DialogNewEventBinding
import com.starrepublic.meetrix.databinding.DialogSelectRoomBinding
import java.util.*

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