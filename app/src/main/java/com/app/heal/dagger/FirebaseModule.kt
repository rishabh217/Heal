package com.app.heal.dagger

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseDatabaseReference(): DatabaseReference {
       val database =  Firebase.database
        return database.reference
    }
}