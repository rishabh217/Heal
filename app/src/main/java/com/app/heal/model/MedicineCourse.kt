package com.app.heal.model

class MedicineCourse(
    var medicines: HashMap<String, DailyDose>? // MedicineId -> DailyDoses
) {
    constructor() : this(null)
}