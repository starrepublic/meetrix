package com.starrepublic.meetrix2

import android.content.Intent

/**
 * Created by richard on 2016-11-02.
 */
interface BaseView<T> {

    fun setPresenter(presenter:T)
    fun hasPermission(permission:String):Boolean
    fun startActivityForResult(newChooseAccountIntent: Intent?, requestCode: Int)
}