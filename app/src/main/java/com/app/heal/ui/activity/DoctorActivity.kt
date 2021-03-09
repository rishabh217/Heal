package com.app.heal.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.adapter.MedicinesAdapter
import com.app.heal.interfaces.DoctorDetailsCallback
import com.app.heal.interfaces.MedicineCoursesCallback
import com.app.heal.model.DailyDose
import com.app.heal.model.Doctor
import com.app.heal.model.MedStatus
import com.app.heal.model.MedicineCourse
import com.app.heal.utils.Util
import com.app.heal.utils.getDateString
import com.app.heal.utils.roundOffDecimal
import kotlinx.android.synthetic.main.activity_doctor.*
import java.util.*
import kotlin.collections.HashMap

class DoctorActivity : BaseActivity(), MedicineCoursesCallback,
    MedicinesAdapter.Callback, DoctorDetailsCallback {

    private var doctorId = ""
    private lateinit var rvMedicines: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var medicinesAdapter: MedicinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        rvMedicines = findViewById(R.id.rvMedicines)
        linearLayoutManager = LinearLayoutManager(this)
        medicinesAdapter = MedicinesAdapter(this)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            doctorId = bundle.getString("doctorId")!!
        }

        if (doctorId.isNotEmpty()) {
            firebaseManager.getDoctorDetails(doctorId, Util.LISTENER_TYPE_SINGLE_EVENT, this)
            firebaseManager.getMedicineCourses(doctorId, Util.LISTENER_TYPE_VALUE_EVENT, this)
        }

        rvMedicines.adapter = medicinesAdapter
        rvMedicines.layoutManager = linearLayoutManager
    }

    override fun onGetMedicineCourses(medicines: HashMap<String, MedicineCourse>?) {
        var gotPoints = 0.0
        var totalPoint = 0.0
        if (medicines != null) {
            progressBar?.visibility = View.GONE
            val medicineCourse = medicines[doctorId]
            if (medicineCourse != null) {
                val doses = medicineCourse.medicines
                val medicinesArr = arrayListOf<DailyDose>()
                if (doses != null) {
                    for (dose in doses) {
                        val doseVal = dose.value
                        medicinesArr.add(doseVal)
                        val dailyDoseMap = doseVal.dailyDoseMap
                        val medPoints = doseVal.points.toDouble()
                        totalPoint += medPoints
                        if (dailyDoseMap != null) {
                            val todaysMap = dailyDoseMap[getDateString(Date())]
                            if (todaysMap != null) {
                                val timeMap = todaysMap.doseMap
                                if (timeMap != null) {
                                    val doseCount = timeMap.size
                                    for (time in timeMap) {
                                        if (time.value == MedStatus.Taken)
                                            gotPoints += (medPoints / doseCount)
                                    }
                                }
                            }
                        }
                    }
                    medicinesAdapter.setMedicinesList(medicinesArr)
                }
            }
            medicinesProgress?.progress = (100.0 * gotPoints / totalPoint).toFloat()
        }
    }

    override fun onMedicineCardClick(points: Double, time: String, medicineId: String, status: MedStatus) {
        if (status == MedStatus.Taken)
            firebaseManager.updateUserPoints(roundOffDecimal(points))
        firebaseManager.updateMedicineStatus(doctorId, medicineId, time, status)
    }

    override fun onGetDoctorDetails(doctor: Doctor?) {
        if (doctor != null) {
            docName?.text = doctor.name
        }
    }
}