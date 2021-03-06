package com.app.heal.dagger

import com.app.heal.HealApplication
import com.app.heal.ui.activity.BaseActivity
import com.app.heal.ui.fragments.BaseFragment
import dagger.Component
import javax.inject.Singleton

// Definition of the Application graph
@Singleton
@Component(modules = [FirebaseModule::class, ManagerModule::class, ContextModule::class])
interface ApplicationComponent {

    fun inject(healApplication: HealApplication)
    fun inject(activity: BaseActivity)
    fun inject(fragment: BaseFragment)

}
