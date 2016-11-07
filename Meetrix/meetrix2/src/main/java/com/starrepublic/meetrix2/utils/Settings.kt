package com.starrepublic.meetrix2.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by richard on 2016-11-05.
 */
class Settings(val context: Context) {

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init{
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

    }

    var accountName: String?
        get() = preferences.getString(Settings::accountName.name, null)
        set(value) {
            editor.putString(Settings::accountName.name, value).commit()
        }

    var roomResourceId: String?
        get() = preferences.getString(Settings::roomResourceId.name, null)
        set(value) {
            editor.putString(Settings::roomResourceId.name, value).commit()
        }


}