package com.app.heal.model

class Prescription(
    var date: String, // Date of prescription
    var status: Status // PrescriptionStatus
) {
    constructor() : this("", Status.InProgress)
}