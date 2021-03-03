package com.app.heal.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import com.app.heal.activity.AddDetailsActivity
import com.app.heal.activity.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*

fun randomAlphaString(size: Int): String {
    val ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXY0123456789Zabcdefghijklmnopqrstuvwxyz"
    var count = size
    val builder = StringBuilder()
    while (count-- != 0) {
        val character = (Math.random() * ALPHA_STRING.length).toInt()
        builder.append(ALPHA_STRING[character])
    }
    return "$builder"
}

fun animateView(vararg views: View?) {
    for (view in views) {
        val scaleAnimation = ScaleAnimation(
                0.7f,
                1.0f,
                0.7f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        )
        scaleAnimation.duration = 500
        val bounceInterpolator = BounceInterpolator()
        scaleAnimation.interpolator = bounceInterpolator
        view?.startAnimation(scaleAnimation)
    }
}

fun Context.login() {
    val intent: Intent = if (FirebaseAuth.getInstance().currentUser != null) {
        Intent(this, AddDetailsActivity::class.java)
    } else {
        Intent(this, SignInActivity::class.java)
    }
    intent.apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}

fun getStartOfDay(date: Date): Long {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeZone = TimeZone.getTimeZone("IST")
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}