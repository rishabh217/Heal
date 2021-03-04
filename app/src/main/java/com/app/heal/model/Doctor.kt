package com.app.heal.model

class Doctor(
    var name: String, // Doctor Name
    var prescriptions: HashMap<String, Prescription>?, // PrescriptionId -> Prescription
    var medicines: HashMap<String, Medicine>? // MedicineId -> MedicineDetails
) {
    constructor() : this("", null, null)
}