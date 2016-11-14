package com.starrepublic.meetrix2

import android.app.Application
import android.content.Context
import android.os.Handler
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.injections.AppModule
import com.starrepublic.meetrix2.injections.DaggerAppComponent

/**
 * Created by richard on 2016-11-02.
 */


class App() : MultiDexApplication() {


     var appComponent: AppComponent? = null


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate();


        appComponent = DaggerAppComponent.builder().appModule(AppModule(this, Handler())).build();
    }
}


/*
class App : Application(){



    companion object{
        lateinit var eventsRepositoryComponent: GoogleApiRepositoryComponent
    }


    override fun onCreate(){
        super.onCreate()

        eventsRepositoryComponent = DaggerEventsRepositoryComponent.builder().appModule(AppModule(this)).build();
    }

}*/


