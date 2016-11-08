package com.starrepublic.meetrix2.mvp

import android.content.Intent
import android.os.Bundle

/**
 * Created by richard on 2016-11-07.
 */
interface BaseViewModel {

    fun showToast(message: String)
    fun showSnackbar(message: String)
    fun hasPermission(permission: String): Boolean
    fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?)
}