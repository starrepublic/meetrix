package com.starrepublic.meetrix2

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by richard on 2016-11-03.
 */
@Module
class AppModule(val app: MeetrixApp) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app;
    }

    @Provides
    @Singleton
    fun provideApplication(): MeetrixApp {
        return app;
    }
}