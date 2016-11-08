package com.starrepublic.meetrix2.data

import com.starrepublic.meetrix2.CalendarModule
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.injections.AppModule
import com.starrepublic.meetrix2.mvp.scopes.PerApplication
import dagger.Component
import javax.inject.Singleton

/**
 * Created by richard on 2016-11-03.
 */

@Singleton
@Component(modules = arrayOf(CalendarModule::class, AppModule::class))
interface EventsRepositoryComponent {

    fun getEventsRepository(): EventsRepository

}