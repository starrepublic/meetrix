package com.starrepublic.meetrix.events;

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.api.services.admin.directory.model.CalendarResource
import com.starrepublic.meetrix.BR
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.databinding.DialogSelectRoomBinding
import java.util.*
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager
import com.starrepublic.meetrix.mvp.BaseDialogFragment
import com.starrepublic.meetrix.utils.AlphanumComparator
import com.starrepublic.meetrix.utils.BroadcastEvents


/**
 * Created by richard on 2016-11-09.
 */

class SelectRoomDialogFragment : BaseDialogFragment() {


    companion object {
        fun newInstance() = SelectRoomDialogFragment()
    }

    private lateinit var binding: DialogSelectRoomBinding
    private val items: ArrayList<String> = ArrayList()
    private var resources: List<CalendarResource>? = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private val comparator = AlphanumComparator()


    private val fragment: EventsFragment by lazy {
        (this.targetFragment as EventsFragment)
    }

    private val vm = SelectRoomDialogViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroyView() {
        val dialog = dialog
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        fragment.presenter?.loadRooms()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun showRooms(resources: List<CalendarResource>?) {
        items.clear()
        resources?.forEach {
            items.add(it.resourceName)
        }
        Collections.sort(items, comparator)
        this.resources = resources
        vm.loading = false
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding = DataBindingUtil.inflate<DialogSelectRoomBinding>(inflater, R.layout.dialog_select_room, container, false);

        adapter = ArrayAdapter<String>(context,R.layout.list_item_room, items);

        binding.setVariable(BR.viewModel, vm)
        dialog.setTitle(R.string.select_room)

        binding.listRooms?.adapter = adapter
        binding.listRooms?.setOnItemClickListener { adapterView, view, i, l ->

            val name = items.get(i)

            fragment.presenter?.room = resources?.find({
                it.resourceName == name
            })
            dismiss()
            fragment.presenter?.loadEvents()

        }

        return binding.root
    }


    /*
    override fun show(manager: FragmentManager?, tag: String?) {

        val window = activity.window
        // Set the dialog to not focusable.
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility;
        }

        super.show(manager, tag)

        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }*/


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog:Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.select_room)
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        BroadcastEvents.send(context, Intent(BroadcastEvents.dialogClosedEvent))
    }
}

