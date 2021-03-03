package com.app.heal.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.heal.HealApplication
import com.app.heal.utils.AppPreferences
import com.app.heal.utils.FirebaseManager
import com.app.heal.utils.LocationManager
import com.google.firebase.iid.FirebaseInstanceId
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.applicationContext as HealApplication).appComponent.inject(this)

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this
            ) { instanceIdResult ->
                firebaseManager.updateToken(instanceIdResult.token)
            }

    }

}