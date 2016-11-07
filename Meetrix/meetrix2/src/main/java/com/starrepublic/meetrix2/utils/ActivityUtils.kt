package com.starrepublic.meetrix2.utils

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

/**
 * Created by richard on 2016-11-04.
 */


fun FragmentActivity.addFragmentToActivity(fragment: Fragment?, frameId:Int){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(frameId,fragment)
    transaction.commit()

}