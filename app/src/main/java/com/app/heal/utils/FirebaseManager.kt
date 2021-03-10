package com.app.heal.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.app.heal.interfaces.*
import com.app.heal.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Singleton
class FirebaseManager @Inject constructor(
    var database: DatabaseReference
) {

    fun updateToken(token: String) {
        database.child("tokens")
            .child(getSelfUId()).setValue(token)
    }

    fun updateUserDetails(user: User) {
        if (user.userId.isEmpty())
            return
        val userReference = database.child("users").child(user.userId)
        if (user.name.isNotEmpty())
            userReference.child("name").setValue(user.name)
        if (user.email.isNotEmpty())
            userReference.child("email").setValue(user.email)
        if (user.age != 0L)
            userReference.child("age").setValue(user.age)
        if (user.gender != Gender.None)
            userReference.child("gender").setValue(user.gender)
        if (user.phone.isNotEmpty())
            userReference.child("phone").setValue(user.phone)
        if (user.userId.isNotEmpty())
            userReference.child("userId").setValue(user.userId)
        if (user.location != null)
            userReference.child("location").setValue(user.location)
        if (user.doctors != null)
            userReference.child("doctors").setValue(user.doctors)
        if (user.medicineCourses != null)
            userReference.child("medicineCourses").setValue(user.medicineCourses)
    }

    fun uploadPrescription(
        context: Context?,
        filePath: Uri,
        doctorId: String,
        imageUploadStatusCallback: ImageUploadStatusCallback
    ) {

        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val imageId = UUID.randomUUID().toString()
        val ref = storageReference.child("prescriptions/" + imageId)
        //Compress Image
        val bmp: Bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 35, baos)
        val data: ByteArray = baos.toByteArray()

        val uploadTask = ref?.putBytes(data)
        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> // Get a URL to the uploaded content
            var imageLink = ""
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                uploadImageToFirebase(doctorId, imageId, it.toString())
                imageUploadStatusCallback.onImageUploaded(true, it.toString())
            }
        })
            .addOnFailureListener(OnFailureListener {
                // Handle unsuccessful uploads
                // ...
                imageUploadStatusCallback.onImageUploaded(false, "")
            })
            .addOnProgressListener { }
    }

    private fun uploadImageToFirebase(doctorId: String, imageId: String, imageLink: String) {
        if (imageId.isNotEmpty()) {
            val prescriptionReference = database.child("users")
                .child(getSelfUId())
                .child("doctors").child(doctorId)
                .child("prescription").child(imageId)
            prescriptionReference.child("url").setValue(imageLink)
            prescriptionReference.child("status").setValue(Status.InProgress)
            prescriptionReference.child("date").setValue(getDateString(Date()))
        }
    }

    fun getUserDetails(userId: String, userDetailsCallback: UserDetailsCallback) {
        if (userId.isEmpty())
            userDetailsCallback.onGetUserDetails(null)

        val userReference = database.child("users").child(userId)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val user = User()
                    val userMap = dataSnapshot.value as HashMap<String, Any?>
                    user.name = if (userMap["name"] != null) userMap["name"] as String else ""
                    user.phone = if (userMap["phone"] != null) userMap["phone"] as String else ""
                    user.email = if (userMap["email"] != null) userMap["email"] as String else ""
                    user.age = if (userMap["age"] != null) userMap["age"] as Long else 0L
                    user.gender = if (userMap["gender"] != null) Gender.valueOf(userMap["gender"] as String) else Gender.None
                    user.userId = if (userMap["userId"] != null) userMap["userId"] as String else ""
                    user.points =
                        if (userMap["points"] != null)
                            try { userMap["points"] as Double }
                            catch (ex: ClassCastException) { (userMap["points"] as Long).toDouble() }
                        else 0.0
                    val location = userMap["location"] as? HashMap<String, Double>
                    if (location != null) {
                        val latLng = LatLng()
                        latLng.latitude = if (location["latitude"] != null) location["latitude"]!! else 0.0
                        latLng.longitude = if (location["longitude"] != null) location["longitude"]!! else 0.0
                        user.location = latLng
                    }
                    val doctorsMap = userMap["doctors"] as? HashMap<String, Any?>
                    user.doctors = getDoctorsHashMapFromDataSnapshot(doctorsMap)
                    val medicineCoursesMap = userMap["medicineCourses"] as? HashMap<String, Any?>
                    user.medicineCourses = getMedicineCoursesHashMapFromDataSnapshot(medicineCoursesMap)
                    val doctorIdsMap = userMap["doctorIds"] as? ArrayList<String>
                    if (doctorIdsMap != null) {
                        user.doctorIds = doctorIdsMap
                    }
                    userDetailsCallback.onGetUserDetails(user)
                } else
                    userDetailsCallback.onGetUserDetails(null)
            }
        }
        userReference.addListenerForSingleValueEvent(postListener)
    }

    fun getDoctorDetails(doctorId: String, listenerType: String, doctorDetailsCallback: DoctorDetailsCallback) {
        if (doctorId.isEmpty())
            doctorDetailsCallback.onGetDoctorDetails(null)

        val doctorReference =
            database.child("users").child(getSelfUId())
                .child("doctors").child(doctorId)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val docMapValue = dataSnapshot.value as Any
                    val docMap = hashMapOf<String, Any?>()
                    docMap[doctorId] = docMapValue
                    val docHashMap = getDoctorsHashMapFromDataSnapshot(docMap)
                    if (docHashMap != null)
                        doctorDetailsCallback.onGetDoctorDetails(docHashMap[doctorId])
                    else
                        doctorDetailsCallback.onGetDoctorDetails(null)
                } else
                    doctorDetailsCallback.onGetDoctorDetails(null)
            }
        }
        if (listenerType == Util.LISTENER_TYPE_SINGLE_EVENT) doctorReference.addListenerForSingleValueEvent(postListener)
        else doctorReference.addValueEventListener(postListener)
    }

    fun getMedicineCourses(doctorId: String, listenerType: String, medicineCoursesCallback: MedicineCoursesCallback) {
        if (doctorId.isEmpty())
            medicineCoursesCallback.onGetMedicineCourses(null)

        val medicineCourseReference =
            database.child("users").child(getSelfUId())
                .child("medicineCourses").child(doctorId)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val medCourseMapValue = dataSnapshot.value as Any
                    val medCourseMap = hashMapOf<String, Any?>()
                    medCourseMap[doctorId] = medCourseMapValue
                    medicineCoursesCallback.onGetMedicineCourses(getMedicineCoursesHashMapFromDataSnapshot(medCourseMap))
                } else
                    medicineCoursesCallback.onGetMedicineCourses(null)
            }
        }
        if (listenerType == Util.LISTENER_TYPE_SINGLE_EVENT) medicineCourseReference.addListenerForSingleValueEvent(postListener)
        else medicineCourseReference.addValueEventListener(postListener)
    }

    private fun getDoctorsHashMapFromDataSnapshot(doctorsMap: HashMap<String, Any?>?): HashMap<String, Doctor>? {
        if (doctorsMap != null) {
            val doctorsHashMap = hashMapOf<String, Doctor>()
            for (doctor in doctorsMap) {
                if (doctor.value != null) {
                    val docMap = doctor.value as HashMap<String, Any?>
                    val docNode = Doctor()
                    docNode.doctorId = doctor.key
                    docNode.name = if (docMap["name"] != null) docMap["name"] as String else ""
                    if (docMap["prescription"] != null) {
                        val prescriptionHashMap = hashMapOf<String, Prescription>()
                        val prescriptionMap = docMap["prescription"] as? HashMap<String, Any?>
                        val prescriptionNode = Prescription()
                        if (prescriptionMap != null) {
                            val prescription = prescriptionMap.values?.toTypedArray()?.get(0) as? HashMap<String, Any?>
                            if (prescription != null) {
                                prescriptionNode.prescriptionId = prescriptionMap.keys?.toTypedArray()?.get(0)
                                prescriptionNode.url = if (prescription["url"] != null) prescription["url"] as String else ""
                                prescriptionNode.date = if (prescription["date"] != null) prescription["date"] as String else ""
                                prescriptionNode.status = if (prescription["status"] != null) Status.valueOf(prescription["status"] as String) else Status.NotUploaded
                            }
                        }
                        if (prescriptionNode.prescriptionId.isNotEmpty()) {
                            prescriptionHashMap[prescriptionNode.prescriptionId] = prescriptionNode
                            docNode.prescription = prescriptionHashMap
                        } else {
                            docNode.prescription = null
                        }
                    }
                    if (docMap["medicines"] != null) {
                        val medicinesHashMap = hashMapOf<String, Medicine>()
                        val medicinesMap = docMap["medicines"] as? HashMap<String, Any?>
                        if (medicinesMap != null) {
                            for (medicine in medicinesMap) {
                                val medNode = Medicine()
                                val medMap = medicine.value as? HashMap<String, Any?>
                                if (medMap != null) {
                                    medNode.medicineId = medicine.key
                                    medNode.name = if (medMap["name"] != null) medMap["name"] as String else ""
                                    medNode.manufacturer = if (medMap["manufacturer"] != null) medMap["manufacturer"] as String else ""
                                    medNode.dosePerDay =
                                        if (medMap["dosePerDay"] != null)
                                            try { medMap["dosePerDay"] as Long }
                                            catch (ex: ClassCastException) { (medMap["dosePerDay"] as Double).toLong() }
                                        else 0L
                                    medNode.points =
                                        if (medMap["points"] != null)
                                            try { medMap["points"] as Long }
                                            catch (ex: ClassCastException) { (medMap["points"] as Double).toLong() }
                                        else 0L
                                    medNode.power =
                                        if (medMap["power"] != null)
                                            try { medMap["power"] as Double }
                                            catch (ex: ClassCastException) { (medMap["power"] as Long).toDouble() }
                                        else 0.0
                                }
                                if (medNode.medicineId.isNotEmpty())
                                    medicinesHashMap[medNode.medicineId] = medNode
                            }
                        }
                        docNode.medicines = if (medicinesHashMap.isNotEmpty()) medicinesHashMap else null
                    }
                    doctorsHashMap[docNode.doctorId] = docNode
                }
            }
            return doctorsHashMap
        } else {
            return null
        }
    }

    private fun getMedicineCoursesHashMapFromDataSnapshot(medicineCoursesMap: HashMap<String, Any?>?): HashMap<String, MedicineCourse>? {
        if (medicineCoursesMap != null) {
            val medicineCoursesHashMap = hashMapOf<String, MedicineCourse>()
            for (medCourse in medicineCoursesMap) {
                if (medCourse.value != null) {
                    val medCourseNode = MedicineCourse()
                    medCourseNode.doctorId = medCourse.key
                    val doctorIdMap = medCourse.value as? HashMap<String, Any?>
                    if (doctorIdMap != null) {
                        val medicinesMap = doctorIdMap["medicines"] as? HashMap<String, Any?>
                        val medicinesHashMap = hashMapOf<String, DailyDose>()
                        if (medicinesMap != null) {
                            for (medicine in medicinesMap) {
                                val dailyDoseNode = DailyDose()
                                if (medicine.value != null) {
                                    val dailyDoseMap = medicine.value as? HashMap<String, Any?>
                                    dailyDoseNode.medicineId = medicine.key
                                    if (dailyDoseMap != null) {
                                        dailyDoseNode.name = if (dailyDoseMap["name"] != null) dailyDoseMap["name"] as String else ""
                                        dailyDoseNode.points =
                                            if (dailyDoseMap["points"] != null)
                                                try { dailyDoseMap["points"] as Long }
                                                catch (ex: ClassCastException) { (dailyDoseMap["points"] as Double).toLong() }
                                            else 0L
                                        val dailyMap = dailyDoseMap["dailyDoseMap"] as? HashMap<String, Any?>
                                        val dailyHashMap = hashMapOf<String, Dose>()
                                        if (dailyMap != null) {
                                            for (day in dailyMap) {
                                                val doseMap = day.value as? HashMap<String, Any?>
                                                val doseNode = Dose()
                                                val doseHashMap = hashMapOf<String, MedStatus>()
                                                if (doseMap != null) {
                                                    doseNode.date = day.key
                                                    val dateMap = doseMap["doseMap"] as HashMap<String, String?>
                                                    for (dose in dateMap) {
                                                        if (dose.value != null) {
                                                            doseHashMap[dose.key] = MedStatus.valueOf(dose.value as String)
                                                        }
                                                    }
                                                }
                                                doseNode.doseMap = if (doseHashMap.isNotEmpty()) doseHashMap else null
                                                dailyHashMap[doseNode.date] = doseNode
                                            }
                                        }
                                        dailyDoseNode.dailyDoseMap = dailyHashMap
                                    }
                                    medicinesHashMap[dailyDoseNode.medicineId] = dailyDoseNode
                                }
                            }
                            medCourseNode.medicines = if (medicinesHashMap.isNotEmpty()) medicinesHashMap else null
                        }
                    }
                    medicineCoursesHashMap[medCourseNode.doctorId] = medCourseNode
                }
            }
            return medicineCoursesHashMap
        } else {
            return null
        }
    }

    fun getPrescriptionDetails(doctorId: String, listenerType: String, prescriptionDetailsCallback: PrescriptionDetailsCallback) {
        if (doctorId.isEmpty())
            prescriptionDetailsCallback.onGetPrescriptionDetails(null)

        val prescriptionReference =
            database.child("users").child(getSelfUId())
                .child("doctors").child(doctorId).child("prescription")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val prescriptionMap = dataSnapshot.value as? HashMap<String, Any?>
                    val prescriptionNode = Prescription()
                    if (prescriptionMap != null) {
                        val prescription = prescriptionMap.values?.toTypedArray()?.get(0) as? HashMap<String, Any?>
                        if (prescription != null) {
                            prescriptionNode.prescriptionId = prescriptionMap.keys?.toTypedArray()?.get(0)
                            prescriptionNode.url = if (prescription["url"] != null) prescription["url"] as String else ""
                            prescriptionNode.date = if (prescription["date"] != null) prescription["date"] as String else ""
                            prescriptionNode.status = if (prescription["status"] != null) Status.valueOf(prescription["status"] as String) else Status.NotUploaded
                        }
                    }
                    if (prescriptionNode.prescriptionId.isNotEmpty())
                        prescriptionDetailsCallback.onGetPrescriptionDetails(prescriptionNode)
                    else
                        prescriptionDetailsCallback.onGetPrescriptionDetails(null)
                } else
                    prescriptionDetailsCallback.onGetPrescriptionDetails(null)
            }
        }
        if (listenerType == Util.LISTENER_TYPE_SINGLE_EVENT) prescriptionReference.addListenerForSingleValueEvent(postListener)
        else prescriptionReference.addValueEventListener(postListener)
    }

    fun isNewUser(newUserCheckCallback: NewUserCheckCallback) {
        // TODO: 06-03-2021 May be a better way to do this
        val userReference =
            database.child("users").child(getSelfUId())
                .child("name")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    newUserCheckCallback.onGetCheckDetails(false)
                } else
                    newUserCheckCallback.onGetCheckDetails(true)
            }
        }
        userReference.addListenerForSingleValueEvent(postListener)
    }

    fun getFirstDoctorId(firstDoctorIdCallback: FirstDoctorIdCallback) {
        val doctorsReference =
            database.child("users").child(getSelfUId())
                .child("doctorIds")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val doctorIds = dataSnapshot.value as ArrayList<String>
                    firstDoctorIdCallback.onGetFirstDoctorId(doctorIds[0])
                } else
                    firstDoctorIdCallback.onGetFirstDoctorId(null)
            }
        }
        doctorsReference.addListenerForSingleValueEvent(postListener)
    }

    fun updateDoctorId(doctorId: String) {
        if (doctorId.isEmpty())
            return
        val doctorIdsReference =
            database.child("users").child(getSelfUId())
                .child("doctorIds")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val doctorIds = dataSnapshot.value as ArrayList<String>
                    doctorIds.add(doctorId)
                    doctorIdsReference.setValue(doctorIds)
                } else {
                    val doctorIds = arrayListOf<String>()
                    doctorIds.add(doctorId)
                    doctorIdsReference.setValue(doctorIds)
                }
            }
        }
        doctorIdsReference.addListenerForSingleValueEvent(postListener)
    }

    fun updateMedicineStatus(doctorId: String, medicineId: String, time: String, status: MedStatus) {
        if(doctorId.isEmpty() || medicineId.isEmpty() || time.isEmpty())
            return
        val medicineStatusReference =
            database.child("users").child(getSelfUId())
                .child("medicineCourses").child(doctorId)
                .child("medicines").child(medicineId)
                .child("dailyDoseMap").child(getDateString(Date()))
                .child("doseMap").child(time)
        medicineStatusReference.setValue(status)
    }

    fun fetchUserPoints(userPointsCallback: UserPointsCallback) {
        val userPointsReference =
            database.child("users").child(getSelfUId()).child("points")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val points = try { dataSnapshot.value as Double } catch (ex: ClassCastException) { (dataSnapshot.value as Long).toDouble() }
                    userPointsCallback.onGetUserPoints(points)
                } else {
                    userPointsCallback.onGetUserPoints(0.0)
                }
            }
        }
        userPointsReference.addListenerForSingleValueEvent(postListener)
    }

    fun updateUserPoints(mPoints: Double) {
        val userPointsReference =
            database.child("users").child(getSelfUId())
                .child("points")
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    var points = try { dataSnapshot.value as Double } catch (ex: ClassCastException) { (dataSnapshot.value as Long).toDouble() }
                    points += mPoints
                    userPointsReference.setValue(points)
                } else {
                    userPointsReference.setValue(mPoints)
                }
            }
        }
        userPointsReference.addListenerForSingleValueEvent(postListener)
    }

    fun setContactUserDetails(userId: String, contact: ContactUser) {
        if (userId.isEmpty() || contact.phone.isEmpty())
            return
        val contactUserReference =
            database.child("contactUsers").child(userId)
                .child(randomAlphaString(5))
        contactUserReference.setValue(contact)
    }

    fun getDisplayInformation(type: String, displayInformationCallback: DisplayInformationCallback) {
        val infoReference =
            database.child("information").child(type)
        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val info = dataSnapshot.value as ArrayList<String?>
                    displayInformationCallback.onGetInformation(info)
                }
            }
        }
        infoReference.addListenerForSingleValueEvent(postListener)
    }

    fun addDoctor(doctorId: String, doctor: Doctor, medicineCourse: MedicineCourse) {
        val userReference =
            database.child("users").child(getSelfUId())
        userReference.child("doctors").child(doctorId).setValue(doctor)
        userReference.child("medicineCourses").child(doctorId).setValue(medicineCourse)
    }

}