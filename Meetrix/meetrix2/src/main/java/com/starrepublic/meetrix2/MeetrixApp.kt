package com.starrepublic.meetrix2

import android.app.Application
import com.starrepublic.meetrix2.data.DaggerEventsRepositoryComponent
import com.starrepublic.meetrix2.data.EventsRepositoryComponent

/**
 * Created by richard on 2016-11-02.
 */

class MeetrixApp : Application(){



    companion object{
        lateinit var eventsRepositoryComponent: EventsRepositoryComponent
    }


    override fun onCreate(){
        super.onCreate()

        eventsRepositoryComponent = DaggerEventsRepositoryComponent.builder().appModule(AppModule(this)).build();
    }

}


