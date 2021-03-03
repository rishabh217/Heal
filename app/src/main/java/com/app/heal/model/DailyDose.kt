package com.app.heal.model

class DailyDose(
    var dailyDoseMap: HashMap<String, Dose>? // Date -> Doses
) {
    constructor() : this(null)
}