package com.app.heal.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.heal.HealApplication
import com.app.heal.utils.AppPreferences
import com.app.heal.utils.FirebaseManager
import com.app.heal.utils.LocationManager
import javax.inject.Inject

open class BaseFragment : Fragment() {

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var locationManager: LocationManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as HealApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}