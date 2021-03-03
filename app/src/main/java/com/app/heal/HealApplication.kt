package com.app.heal

import android.app.Application
import com.app.heal.dagger.ContextModule
import com.app.heal.dagger.DaggerApplicationComponent
import com.app.heal.utils.AppPreferences
import com.app.heal.utils.FirebaseManager
import javax.inject.Inject


class HealApplication : Application() {

    val appComponent =
        DaggerApplicationComponent.builder().contextModule(ContextModule(this)).build()

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)

    }

}