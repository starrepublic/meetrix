package com.starrepublic.meetrix.events

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.starrepublic.meetrix.BR
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.databinding.DialogNewEventBinding
import com.starrepublic.meetrix.mvp.BaseDialogFragment
import com.starrepublic.meetrix.utils.BroadcastEvents
import com.starrepublic.meetrix.utils.setImmersiveMode
import java.text.SimpleDateFormat
import java.util.*

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

        arguments?.let {
            from = Date(it.getLong(EXTRA_FROM))
            to = Date(it.getLong(EXTRA_TO))
        }

        setImmersiveMode(true)
    }

    private lateinit var binding: DialogNewEventBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate<DialogNewEventBinding>(LayoutInflater.from(context), R.layout.dialog_new_event, null, false);

        binding.txtEventName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        binding.txtEventName.setOnEditorActionListener({ v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createEvent()
                dismiss()
                handled = true
            }
            handled
        })

        vm.from = DATE_FORMAT.format(from)
        vm.to = DATE_FORMAT.format(to)

        vm.eventName = getString(R.string.event_name_default_value)

        binding.setVariable(BR.viewModel, vm)
        val dialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.new_event)
                .setPositiveButton(R.string.create,
                        { dialog, whichButton ->
                            createEvent()
                        }
                )
                .setView(binding.root)
                .setNegativeButton(R.string.cancel)
                { dialog, whichButton ->
                    dialog.dismiss()
                }
                .create()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (!vm.eventName.isNullOrBlank()) {
            dialog.setOnShowListener { binding.txtEventName.selectAll() }
        }

        return dialog
    }

    private fun createEvent() {
        fragment.presenter?.createEvent(vm.eventName, from, to, fragment.presenter?.accountName!!)
    }

    override fun onDestroyView() {
        val dialog = dialog
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        fragment.dismissedDialog(this)
        context?.let {
            BroadcastEvents.send(it, Intent(BroadcastEvents.dialogClosedEvent))
        }

    }
}
