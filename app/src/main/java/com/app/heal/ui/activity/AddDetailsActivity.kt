package com.app.heal.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.app.heal.R
import com.app.heal.model.*
import com.app.heal.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_details.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddDetailsActivity : BaseActivity() {

    private var mGender = Gender.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        val etMedArr = arrayListOf<EditText>(findViewById(R.id.med1), findViewById(R.id.med2), findViewById(R.id.med3), findViewById(R.id.med4))
        val etManArr = arrayListOf<EditText>(findViewById(R.id.man1), findViewById(R.id.man2), findViewById(R.id.man3), findViewById(R.id.man4))
        val etDoseArr = arrayListOf<EditText>(findViewById(R.id.dose1), findViewById(R.id.dose2), findViewById(R.id.dose3), findViewById(R.id.dose4))

        inputMedsText?.setOnClickListener {
            inputMedsText?.visibility = View.GONE
            medsLayout?.visibility = View.VISIBLE
        }

        male?.setOnClickListener {
            animateView(male)
            male?.setBackgroundResource(R.drawable.text_male_selected)
            female?.setBackgroundResource(R.drawable.text_female_not_selected)
            mGender = Gender.Male
        }

        female?.setOnClickListener {
            animateView(female)
            female?.setBackgroundResource(R.drawable.text_female_selected)
            male?.setBackgroundResource(R.drawable.text_male_not_selected)
            mGender = Gender.Female
        }

        save.setOnClickListener {

            val medIdArr = arrayListOf<String>("", "", "", "")
            val medArr = arrayListOf<String>("", "", "", "")
            val manArr = arrayListOf<String>("", "", "", "")
            val doseArr = arrayListOf<String>("", "", "", "")

            for (idx in 0..3) {
                medArr[idx] = if (etMedArr[idx].text.toString().isNotEmpty()) etMedArr[idx].text.toString() else ""
                manArr[idx] = if (etManArr[idx].text.toString().isNotEmpty()) etManArr[idx].text.toString() else ""
                doseArr[idx] = if (etDoseArr[idx].text.toString().isNotEmpty()) etDoseArr[idx].text.toString() else ""
            }

            if (name.text.toString().isEmpty() || dname.text.toString().isEmpty() || age.text.toString().toLong() <= 0L || mGender == Gender.None) {
                setSnackBar(findViewById(android.R.id.content), "Enter all required information")
                return@setOnClickListener
            }

            for (idx in 0..3) {
                if (medArr[idx].isNotEmpty() && doseArr[idx].isEmpty()) {
                    etDoseArr[idx].error = "Required"
                    return@setOnClickListener
                }
            }

            val authEmail = FirebaseAuth.getInstance().currentUser?.email
            email.setText(authEmail?.toString() ?: "")

            val doctorId = randomAlphaString(7)
            for (idx in 0..3) {
                medIdArr[idx] = if (medArr[idx].isNotEmpty()) randomAlphaString(5) else ""
            }

            val medMap = HashMap<String, Medicine>()
            for (idx in 0..3) {
                if (medIdArr[idx].isNotEmpty()) {
                    val medicine = Medicine()
                    medicine.name = medArr[idx]
                    medicine.manufacturer = manArr[idx]
                    medicine.points = 10
                    medicine.dosePerDay = try { doseArr[idx].toDouble().toLong() } catch (ex: ClassCastException) { doseArr[idx].toLong() }
                    medMap[medIdArr[idx]] = medicine
                }
            }

            val doctor = Doctor()
            doctor.name = dname.text.toString()
            doctor.medicines = medMap

            val user = User()
            val docMap = hashMapOf<String, Doctor>()
            docMap[doctorId] = doctor
            user.doctors = docMap
            user.name = name.text.toString()
            user.age = age.text.toString().toLong()
            user.gender = mGender
            if (email.text.toString().isNotEmpty()) user.email = email.text.toString()
            if (phone.text.toString().isNotEmpty()) user.phone = phone.text.toString()
            user.userId = getSelfUId()

            // TODO: 04-03-2021
            var date = Date()
            var startOfDay = getStartOfDay(date)
            val doseTimeArr = arrayListOf<Long>(
                (21 * 60 * 60 * 1000),
                (9 * 60 * 60 * 1000),
                (15 * 60 * 60 * 1000)
            )
            val medicineCourse = MedicineCourse()
            val medicineMap = hashMapOf<String, DailyDose>()
            for (idx in 0..3) {
                if (medIdArr[idx].isNotEmpty()) {
                    date = Date()
                    startOfDay = getStartOfDay(date)
                    val dailyDose = DailyDose()
                    dailyDose.points = 10
                    val doseList = ArrayList<Dose>()
                    for (day in 0..6) {
                        val dose = Dose()
                        val doseMap = hashMapOf<String, MedStatus>()
                        val doses = try { doseArr[idx].toDouble().toInt() } catch (ex: ClassCastException) { doseArr[idx].toInt() }
                        for (doseCount in 0 until doses) {
                            val time = startOfDay + doseTimeArr[doseCount]
                            doseMap[time.toString()] = MedStatus.Upcoming
                        }
                        date = getNextDate(date)
                        startOfDay = getStartOfDay(date)
                        dose.doseMap = doseMap
                        doseList.add(dose)
                    }
                    date = Date()
                    val dailyDoseMap = hashMapOf<String, Dose>()
                    for (day in 0..6) {
                        dailyDoseMap[getDateString(date)] = doseList[day]
                        date = getNextDate(date)
                    }
                    dailyDose.name = medArr[idx]
                    dailyDose.dailyDoseMap = dailyDoseMap
                    medicineMap[medIdArr[idx]] = dailyDose
                }
            }
            medicineCourse.medicines = medicineMap

            val medCourseMap = hashMapOf<String, MedicineCourse>()
            medCourseMap[doctorId] = medicineCourse
            user.medicineCourses = medCourseMap

            firebaseManager.updateUserDetails(user)
            firebaseManager.updateDoctorId(doctorId)
            openPrescriptionActivity(doctorId, Util.TASK_TYPE_NEW)
        }

    }

}