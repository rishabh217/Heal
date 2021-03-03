package com.app.heal.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor(
    var database: DatabaseReference
) {

    fun updateToken(token: String) {
        database.child("tokens")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).setValue(token)

    }

}