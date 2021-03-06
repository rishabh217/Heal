package com.app.heal.model

class DailyDose(
    var name: String,
    var medicineId: String,
    var dailyDoseMap: HashMap<String, Dose>? // Date -> Doses
) {
    constructor() : this("", "", null)
}