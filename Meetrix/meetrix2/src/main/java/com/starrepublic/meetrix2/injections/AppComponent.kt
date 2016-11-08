/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix2.injections;

import com.starrepublic.meetrix2.CalendarModule
import com.starrepublic.meetrix2.data.EventsRepository
import com.starrepublic.meetrix2.data.EventsRepositoryComponent
import com.starrepublic.meetrix2.events.EventsPresenter2
import com.starrepublic.meetrix2.mvp.scopes.PerFragment
import javax.inject.Singleton;

import dagger.Component;
import nl.endran.skeleton.fragments.EventsFragmentPresenter
import nl.endran.skeleton.fragments.EventsFragmentView

@Singleton
@Component(modules = arrayOf(AppModule::class, CalendarModule::class))
interface AppComponent {

    fun getEventsFragmentView(): EventsFragmentView

    fun getEventsFragmentPresenter():EventsFragmentPresenter

    fun getEventsPresenter2():EventsPresenter2
}
