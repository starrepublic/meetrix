/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix2.mvp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.mvp.BaseViewModel
import com.starrepublic.meetrix2.utils.getAppComponent
import com.starrepublic.meetrix2.mvp.BaseFragmentPresenter
import com.starrepublic.meetrix2.mvp.BaseFragmentView


abstract class BaseFragment<F: Fragment,VM, P : BaseFragmentPresenter<VM>, V : BaseFragmentView<F, VM, P>> : Fragment() {

    var view: V? = null
    var presenter: P? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val appComponent = inflater!!.context.getAppComponent()
        view = createView(appComponent)


        return view!!.inflate(inflater, container!!, savedInstanceState)
    }

    override fun onViewCreated(androidView: View?, savedInstanceState: Bundle?) {
        view?.androidViewReady()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.deflate()
        view = null
    }

    override fun onResume() {
        super.onResume()
        if (view != null) {
            presenter = createPresenter(context.getAppComponent())
            view?.start(presenter!!, this as F)

        }
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context,
                permission)== PackageManager.PERMISSION_GRANTED
    }


    override fun onPause() {
        super.onPause()
        view?.stop()
        presenter = null
    }

    abstract fun createView(appComponent: AppComponent): V
    abstract fun createPresenter(appComponent: AppComponent): P
}
