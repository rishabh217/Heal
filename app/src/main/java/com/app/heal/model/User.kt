package com.app.heal.model

class User(
    var userId: String,
    var name: String,
    var email: String,
    var phone: String,
    var points: Double,
    var location: LatLng?,
    var doctors: HashMap<String, Doctor>?, // DoctorId -> DoctorInfo
    var doctorIds: ArrayList<String>?, // nth Doctor Added -> DoctorId
    var medicineCourses: HashMap<String, MedicineCourse>? // DoctorId -> MedicineCourse
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        0.0,
        null,
        null,
        null,
        null
    )
}