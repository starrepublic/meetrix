package com.starrepublic.meetrix.events

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import com.starrepublic.meetrix.BR
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.databinding.DialogNewEventBinding
import com.starrepublic.meetrix.databinding.DialogSelectRoomBinding
import com.starrepublic.meetrix.mvp.BaseDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.*
import android.widget.TextView
import com.starrepublic.meetrix.utils.BroadcastEvents
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.starrepublic.meetrix.utils.getStatusBarHeight
import com.starrepublic.meetrix.utils.setImmersiveMode
import timber.log.Timber


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

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        from = Date(arguments.getLong(EXTRA_FROM))
        to = Date(arguments.getLong(EXTRA_TO))

        inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setImmersiveMode(true)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //inputMethodManager.showSoftInput(binding.txtEventName, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {


//        val rootView = dialog.window.decorView.findViewById(android.R.id.content)
//        rootView.viewTreeObserver.addOnGlobalLayoutListener {
//            val window = dialog.window
//            val r = Rect()
//            val view = window.decorView
//            view.getWindowVisibleDisplayFrame(r)
//
//            val keyboardHeight = (r.bottom - r.top) - dialog.window.decorView.rootView.height
//
//            if(r.bottom < context.resources.displayMetrics.heightPixels){
//                window.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP)
//                val p = window.attributes
//                p.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
//                p.x = 200 //dialog.window.decorView.rootView.height/2-dialog.window.decorView.height/2
//                window.attributes = p
//            }
//
//            Timber.i("HEIGHT", keyboardHeight)
//            Timber.i("HEIGHT", r)
//        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private lateinit var binding: DialogNewEventBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DataBindingUtil.inflate<DialogNewEventBinding>(LayoutInflater.from(context), R.layout.dialog_new_event, null, false);

        binding.txtEventName.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
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

        binding.setVariable(BR.viewModel, vm)


        val dialog =  AlertDialog.Builder(context)
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
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog
    }

    private fun createEvent() {
        fragment.presenter?.createEvent(vm.eventName,from,to,fragment.presenter?.accountName!!)
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
        fragment.dismissedDialog(this);
        BroadcastEvents.send(context, Intent(BroadcastEvents.dialogClosedEvent))
    }
}