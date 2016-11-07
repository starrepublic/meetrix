package com.starrepublic.meetrix2

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.starrepublic.meetrix2.utils.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by richard on 2016-11-04.
 */

@Module
class SettingsModule {

    @Provides
    @Singleton
    fun providesSettings(context: Context) = Settings(context)
}
