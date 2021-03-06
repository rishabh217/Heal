package com.app.heal.model

class Prescription(
    var url: String, // Prescription URL
    var prescriptionId: String, // Prescription ID
    var date: String, // Date of prescription
    var status: Status // PrescriptionStatus
) {
    constructor() : this("", "", "", Status.InProgress)
}