package com.app.heal.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.app.heal.R
import com.app.heal.interfaces.NewUserCheckCallback
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : BaseActivity(), NewUserCheckCallback {

    // This is the loading time of the splash screen
    private val SPLASH_TIME_OUT: Long = 1000
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            if (auth.currentUser != null) {
                firebaseManager.isNewUser(this)
            } else {
                val intent = Intent(this, SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }

        }, SPLASH_TIME_OUT)

    }

    override fun onGetCheckDetails(isNewUser: Boolean) {
        val intent = if (isNewUser) Intent(this, AddDetailsActivity::class.java) else Intent(this, HomeActivity::class.java)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}