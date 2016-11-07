package com.starrepublic.meetrix2

import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

/**
 * Created by richard on 2016-11-06.
 */
open class BaseFragment<T:Any> : Fragment(), BaseView<T> {

    private lateinit var presenter:T

    override fun setPresenter(presenter:T) {
        this.presenter = presenter;
    }

    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context,
                permission)=== PackageManager.PERMISSION_GRANTED
    }
}