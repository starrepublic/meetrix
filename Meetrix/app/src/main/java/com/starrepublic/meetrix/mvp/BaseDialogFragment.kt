package com.starrepublic.meetrix.mvp

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.starrepublic.meetrix.R

/**
 * Created by richard on 2016-11-12.
 */
open class BaseDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }
}