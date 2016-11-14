/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix2.injections;

import com.starrepublic.meetrix2.GoogleApiModule
import com.starrepublic.meetrix2.data.GoogleApiRepository
import com.starrepublic.meetrix2.data.GoogleApiRepositoryComponent
import com.starrepublic.meetrix2.events.EventsPresenter
import com.starrepublic.meetrix2.mvp.scopes.PerFragment
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = arrayOf(AppModule::class, GoogleApiModule::class))
interface AppComponent {


    fun getEventsPresenter():EventsPresenter

}
