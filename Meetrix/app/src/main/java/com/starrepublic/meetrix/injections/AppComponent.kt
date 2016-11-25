/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix.injections;

import com.starrepublic.meetrix.GoogleApiModule
import com.starrepublic.meetrix.data.GoogleApiRepository
import com.starrepublic.meetrix.data.GoogleApiRepositoryComponent
import com.starrepublic.meetrix.events.EventsPresenter
import com.starrepublic.meetrix.mvp.scopes.PerFragment
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = arrayOf(AppModule::class, GoogleApiModule::class))
interface AppComponent {


    fun getEventsPresenter():EventsPresenter

}
