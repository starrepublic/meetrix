package com.starrepublic.meetrix2

import android.app.Application
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes
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
class GoogleApiModule {

        private val scopes = arrayOf<String>(CalendarScopes.CALENDAR, DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR, DirectoryScopes.ADMIN_DIRECTORY_USER, DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY)
        private val jsonFactory = JacksonFactory.getDefaultInstance()
        private val transport:HttpTransport = AndroidHttp.newCompatibleTransport();

        @Provides
        @Singleton
        fun provideCredential(context: Context) : GoogleAccountCredential{
                val credential = GoogleAccountCredential.usingOAuth2(
                        context.applicationContext, scopes.asList()).setBackOff(ExponentialBackOff())
                return credential;
        }

        @Provides
        @Singleton
        fun provideCalendar(credential:GoogleAccountCredential): Calendar{
                return Calendar.Builder(
                        transport, jsonFactory, credential).setApplicationName("Meetrix").build();
        }

        @Provides
        @Singleton
        fun provideDirectory(credential:GoogleAccountCredential): Directory {
                return Directory.Builder(
                        transport, jsonFactory, credential).setApplicationName("Meetrix").build();
        }



}
