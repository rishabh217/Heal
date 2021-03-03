package com.app.heal.dagger

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


//provider class for shared pref
@Module
class ContextModule(private val activity: Application) {
    @Singleton
    @Provides
    fun provideSharedPrefences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(activity)
    }

}