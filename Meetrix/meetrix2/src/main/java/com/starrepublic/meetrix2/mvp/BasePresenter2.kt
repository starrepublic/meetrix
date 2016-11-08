package com.starrepublic.meetrix2.mvp

/**
 * Created by richard on 2016-11-08.
 */
abstract class BasePresenter2<V:BaseViewModel>(){



    protected var view: V? = null

    fun start(viewModel:V) {
        this.view = viewModel;
        onStart();
    }

    fun stop() {
        onStop()
        view = null
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