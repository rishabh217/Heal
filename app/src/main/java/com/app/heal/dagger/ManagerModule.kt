package com.app.heal.dagger

import com.app.heal.utils.FirebaseManager
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ManagerModule {
    
    @Provides
    @Singleton
    fun provideFirebaseManager(databaseReference: DatabaseReference): FirebaseManager {
        return FirebaseManager(databaseReference)
    }
    
}