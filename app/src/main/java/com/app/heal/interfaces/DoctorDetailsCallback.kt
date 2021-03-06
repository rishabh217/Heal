package com.app.heal.interfaces

import com.app.heal.model.Doctor

interface DoctorDetailsCallback {
    fun onGetDoctorDetails(doctor: Doctor?)
}