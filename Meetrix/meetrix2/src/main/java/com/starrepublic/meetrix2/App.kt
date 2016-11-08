package com.starrepublic.meetrix2

import android.app.Application
import android.os.Handler
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.injections.AppModule
import com.starrepublic.meetrix2.injections.DaggerAppComponent

/**
 * Created by richard on 2016-11-02.
 */


class App : Application() {


    companion object{
        lateinit var appComponent: AppComponent
    }




    override fun onCreate() {
        super.onCreate();


        appComponent = DaggerAppComponent.builder().appModule(AppModule(this, Handler())).build();
    }
}


/*
class App : Application(){



    companion object{
        lateinit var eventsRepositoryComponent: EventsRepositoryComponent
    }


    override fun onCreate(){
        super.onCreate()

        eventsRepositoryComponent = DaggerEventsRepositoryComponent.builder().appModule(AppModule(this)).build();
    }

}*/


