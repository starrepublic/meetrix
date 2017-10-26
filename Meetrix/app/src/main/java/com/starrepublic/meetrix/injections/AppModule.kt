/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package com.starrepublic.meetrix.injections;

import android.content.Context
import android.os.Handler
import android.support.annotation.NonNull
import com.starrepublic.meetrix.utils.NetworkUtils
import com.starrepublic.meetrix.utils.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

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

    @Singleton
    @Provides
    fun provideNetworkUtils(context: Context) = NetworkUtils(context)

    @Provides
    @Singleton
    fun providesSettings(context: Context) = Settings(context)
}
