package com.starrepublic.meetrix2.events

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R
import me.tatarka.bindingcollectionadapter.ItemView

class SelectRoomDialogViewModel() : BaseObservable() {

    @get:Bindable
    var loading: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.viewModel)
        }


    val items: ObservableList<String> = ObservableArrayList()
    val itemView = ItemView.of(BR.item, R.layout.list_item_room)


}