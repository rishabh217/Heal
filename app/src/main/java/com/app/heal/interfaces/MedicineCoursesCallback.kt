package com.app.heal.interfaces

import com.app.heal.model.MedicineCourse

interface MedicineCoursesCallback {
    fun onGetMedicineCourses(medicines: HashMap<String, MedicineCourse>?)
}