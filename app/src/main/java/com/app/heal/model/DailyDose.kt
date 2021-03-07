package com.app.heal.model

class DailyDose(
    var name: String,
    var points: Long,
    var medicineId: String,
    var dailyDoseMap: HashMap<String, Dose>? // Date -> Doses
) {
    constructor() : this("", 0L, "", null)
}