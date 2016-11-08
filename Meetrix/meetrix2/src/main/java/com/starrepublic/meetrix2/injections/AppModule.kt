/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix2.injections;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.starrepublic.meetrix2.utils.Settings

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule(@NonNull val context: Context, @NonNull val handler: Handler) {


    @Singleton
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun provideHandler() = handler

    @Singleton
    @Provides
    fun provideResources(context: Context) = context.resources

    @Provides
    @Singleton
    fun providesSettings(context: Context) = Settings(context)
}
