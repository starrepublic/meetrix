package com.starrepublic.meetrix2.events

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.starrepublic.meetrix2.BR
import com.starrepublic.meetrix2.R
import com.starrepublic.meetrix2.databinding.DialogNewEventBinding
import com.starrepublic.meetrix2.databinding.DialogSelectRoomBinding
import com.starrepublic.meetrix2.mvp.BaseDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.TextView
import com.starrepublic.meetrix2.utils.BroadcastEvents
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo




/**
 * Created by richard on 2016-11-12.
 */
class NewEventDialogFragment : BaseDialogFragment() {


    companion object {
        val DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val EXTRA_FROM: String = "extra_from"
        val EXTRA_TO: String = "extra_to"

        fun newInstance(from: Date, to: Date): NewEventDialogFragment {

            val fragment = NewEventDialogFragment()

            val args = Bundle()
            args.putLong(EXTRA_FROM, from.time)
            args.putLong(EXTRA_TO, to.time)
            fragment.arguments = args

            return fragment
        }
    }

    val fragment: EventsFragment by lazy {
        (this.targetFragment as EventsFragment)
    }

    private val vm = NewEventDialogViewModel()
    private lateinit var from: Date
    private lateinit var to: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        from = Date(arguments.getLong(EXTRA_FROM))
        to = Date(arguments.getLong(EXTRA_TO))
    }

    private lateinit var binding: DialogNewEventBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DataBindingUtil.inflate<DialogNewEventBinding>(LayoutInflater.from(context), R.layout.dialog_new_event, null, false);

        binding.txtEventName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        binding.txtEventName.setOnEditorActionListener({ v, actionId, event ->
            var handled = false
            if (actionId === EditorInfo.IME_ACTION_DONE) {
                createEvent()
                dismiss()
                handled = true
            }
            handled
        })
        vm.from = DATE_FORMAT.format(from)
        vm.to = DATE_FORMAT.format(to)

        binding.setVariable(BR.viewModel, vm)


        return AlertDialog.Builder(context)
                .setTitle(R.string.new_event)
                .setPositiveButton(R.string.create,
                        { dialog, whichButton ->
                            createEvent()
                        }
                )
                .setView(binding.root)
                .setNegativeButton(R.string.cancel
                ) { dialog, whichButton -> dialog.dismiss() }
                .create()
    }

    private fun createEvent() {
        fragment.presenter?.createEvent(vm.eventName,from,to,fragment.presenter?.accountName!!)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        BroadcastEvents.send(context, Intent(BroadcastEvents.dialogClosedEvent))
    }
}