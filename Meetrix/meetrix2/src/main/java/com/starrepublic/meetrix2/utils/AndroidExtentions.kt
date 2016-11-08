package com.starrepublic.meetrix2.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.starrepublic.meetrix2.App
import com.starrepublic.meetrix2.injections.AppComponent

/**
 * Created by richard on 2016-11-04.
 */


fun FragmentActivity.addFragmentToActivity(fragment: Fragment?, frameId:Int){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(frameId,fragment)
    transaction.commit()

}

fun ContextCompat.checkSelfPermission(context: Context, permission:String): Boolean {
    return ContextCompat.checkSelfPermission(context,
            permission)== PackageManager.PERMISSION_GRANTED
}

fun Context.getAppComponent(): AppComponent {
    return (applicationContext as App).getAppComponent()
}

fun View.getLayoutInflater(): LayoutInflater {
    return LayoutInflater.from(context)
}

fun View.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}