/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix2.mvp

import android.databinding.BaseObservable
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.starrepublic.meetrix2.mvp.BaseFragmentPresenter

abstract class BaseFragmentView<F: Fragment,VM, P : BaseFragmentPresenter<VM>> : BaseObservable() {


    protected var rootView: View? = null
    protected var presenter: P? = null
    protected var fragment:F? = null

    open fun inflate(inflater: LayoutInflater, container: ViewGroup, @SuppressWarnings("unused") savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(getViewId(), container, false)
        return rootView!!
    }

    fun androidViewReady() {
        if (rootView != null) {
            prepare(rootView!!)
        }
    }

    fun deflate() {
        stop()
        rootView = null
    }

    fun start(presenter: P, fragment: F) {
        this.presenter = presenter
        this.fragment = fragment

        val viewModel = getViewModel()
        presenter.start(viewModel)
    }

    fun stop() {
        presenter?.stop()
        presenter = null
    }

    /**
     * Return the view id to be inflated
     */
    @LayoutRes
    protected abstract fun getViewId(): Int

    /**
     * Create and return the implementation of the ViewModel
     */
    protected abstract fun getViewModel(): VM

    /**
     * Convenience method so that the implementation knows when UI widget can be obtained and prepared.
     */
    protected abstract fun prepare(rootView: View);
}
