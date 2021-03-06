package com.app.heal.model

class Medicine(
    var name: String, // Med Name
    var medicineId: String, // Med ID
    var power: Double, // Med Power / Strength
    var points: Long, // Points per day given correspond to the Med
    var manufacturer: String, // Med Manufacturer
    var dosePerDay: Long // Med dose per day
) {
    constructor() : this("", "", 0.0, 0, "", 0)
}