package com.starrepublic.meetrix2

import android.app.Application
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

/**
 * Created by richard on 2016-11-03.
 */


@Module
class CalendarModule{

        private val SCOPES = arrayOf<String>(CalendarScopes.CALENDAR)


        @Provides
        @Singleton
        fun provideCredential(context: Context) : GoogleAccountCredential{
                val credential = GoogleAccountCredential.usingOAuth2(
                        context.applicationContext, SCOPES.asList()).setBackOff(ExponentialBackOff())
                return credential;
        }

        @Provides
        @Singleton
        fun provideCalendar(credential:GoogleAccountCredential): Calendar{


                val transport:HttpTransport = AndroidHttp.newCompatibleTransport();
                val jsonFactory = JacksonFactory.getDefaultInstance()
                return Calendar.Builder(
                        transport, jsonFactory, credential).setApplicationName("Google Calendar API Android Quickstart").build();
        }

}
