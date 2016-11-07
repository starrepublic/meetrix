package com.starrepublic.meetrix2.data

import com.starrepublic.meetrix2.AppModule
import com.starrepublic.meetrix2.CalendarModule
import com.starrepublic.meetrix2.events.EventsFragment
import com.starrepublic.meetrix2.events.EventsPresenter
import com.starrepublic.meetrix2.events.EventsPresenterModule
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