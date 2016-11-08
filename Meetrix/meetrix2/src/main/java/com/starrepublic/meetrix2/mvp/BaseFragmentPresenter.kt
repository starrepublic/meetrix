/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */


package com.starrepublic.meetrix2.mvp

import com.starrepublic.meetrix2.mvp.BaseViewModel

abstract class BaseFragmentPresenter<VM> {

    protected var viewModel: VM? = null

    fun start(viewModel: VM) {
        this.viewModel = viewModel;
        onStart();
    }

    fun stop() {
        onStop()
        viewModel = null
    }

    /**
     * Use this callback to start some operation, like database a query.<br>
     * The viewModel is non-null.
     */
    protected abstract fun onStart();

    /**
     * Stop any running operation that might be busy in the background.<br>
     * After this method is finished viewModel will be de-referenced.
     */
    protected abstract fun onStop();

}
