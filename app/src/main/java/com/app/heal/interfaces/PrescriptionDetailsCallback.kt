package com.app.heal.interfaces

import com.app.heal.model.Prescription

interface PrescriptionDetailsCallback {
    fun onGetPrescriptionDetails(prescription: Prescription?)
}