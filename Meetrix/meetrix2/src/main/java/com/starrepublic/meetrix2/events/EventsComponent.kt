package com.starrepublic.meetrix2.events

import com.starrepublic.meetrix2.data.EventsRepositoryComponent
import com.starrepublic.meetrix2.utils.FragmentScoped
import dagger.Component

/**
 * Created by richard on 2016-11-03.
 */


@FragmentScoped
@Component(dependencies = arrayOf(EventsRepositoryComponent::class), modules = arrayOf(EventsPresenterModule::class))
 interface EventsComponent {

    fun inject(activity: EventsActivity)
    fun getPresenter(): EventsPresenter
}