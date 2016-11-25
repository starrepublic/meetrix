package com.starrepublic.meetrix.mvp

import android.content.Intent
import android.os.Bundle

/**
 * Created by richard on 2016-11-07.
 */
interface BaseViewModel {

    fun showError(error: Throwable?, code:Int=0)
    fun showToast(message: String)
    fun showSnackbar(message: String)
    fun hasPermission(permission: String): Boolean
    fun string(messageId:Int):String?
}