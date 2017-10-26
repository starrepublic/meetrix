package com.starrepublic.meetrix.data

import com.starrepublic.meetrix.GoogleApiModule
import com.starrepublic.meetrix.injections.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by richard on 2016-11-03.
 */
@Singleton
@Component(modules = arrayOf(GoogleApiModule::class, AppModule::class))
interface GoogleApiRepositoryComponent {
    fun getEventsRepository(): GoogleApiRepository
}