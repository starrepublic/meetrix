package com.starrepublic.meetrix2.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

/**
 * Created by richard on 2016-11-05.
 */


fun ContextCompat.checkSelfPermission(context:Context, permission:String): Boolean {
    return ContextCompat.checkSelfPermission(context,
            permission)== PackageManager.PERMISSION_GRANTED
}
