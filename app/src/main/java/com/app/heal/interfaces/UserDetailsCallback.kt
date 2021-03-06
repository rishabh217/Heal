package com.app.heal.interfaces

import com.app.heal.model.User

interface UserDetailsCallback {
    fun onGetUserDetails(user: User?)
}