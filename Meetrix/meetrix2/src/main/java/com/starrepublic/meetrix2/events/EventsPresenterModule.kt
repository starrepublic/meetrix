package com.starrepublic.meetrix2.events

import dagger.Module
import dagger.Provides

/**
 * Created by richard on 2016-11-03.
 */
@Module
class EventsPresenterModule(val view:EventsContract.View) {



    @Provides
    fun provideTasksContractView(): EventsContract.View  = view
}