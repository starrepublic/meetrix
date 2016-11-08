package com.starrepublic.meetrix2.events


import android.app.Dialog
import android.content.Intent
import android.databinding.*
import android.os.Bundle
import android.provider.ContactsContract
import android.renderscript.ScriptGroup
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.App
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.databinding.DialogSelectRoomBinding
import com.starrepublic.meetrix2.injections.AppComponent
import me.tatarka.bindingcollectionadapter.ItemView
import nl.endran.skeleton.fragments.EventsFragmentPresenter
import nl.endran.skeleton.fragments.EventsFragmentView
import com.starrepublic.meetrix2.mvp.BaseFragment
import javax.inject.Inject

/**
 * Created by richard on 2016-11-02.
 */
class EventsFragment : BaseFragment<EventsFragment, EventsFragmentPresenter.ViewModel, EventsFragmentPresenter, EventsFragmentView>() {

    companion object {
        fun newInstance() = EventsFragment()



    }

    override fun createView(appComponent: AppComponent): EventsFragmentView {
        return appComponent.getEventsFragmentView()
    }


    override fun createPresenter(appComponent: AppComponent): EventsFragmentPresenter {
        return appComponent.getEventsFragmentPresenter()
    }


    class SelectRoomDialogFragment : DialogFragment() {


        val fragment : EventsFragment by lazy {
            (this.targetFragment as EventsFragment)
        }

        val vm = SelectRoomDialogViewModel()


        override fun onResume() {
            super.onResume()

            fragment.presenter?.loadRooms()

        }

        fun showRooms(resources:List<Directory.Resources>){
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


            val binding:DialogSelectRoomBinding = DataBindingUtil.inflate<DialogSelectRoomBinding>(inflater, R.layout.dialog_select_room, container, false);

            binding.setVariable(BR.viewModel, vm);
            dialog.setTitle(R.string.select_room);

            return binding.root
        }


    }
}

