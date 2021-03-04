package com.app.heal.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.app.heal.interfaces.ImageUploadStatusCallback
import com.app.heal.model.Status
import com.app.heal.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor(
    var database: DatabaseReference
) {

    fun updateToken(token: String) {
        database.child("tokens")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).setValue(token)
    }

    fun updateUserDetails(user: User) {
        if (user.userId.isEmpty())
            return
        val userReference = database.child("users").child(user.userId)
        if (user.name.isNotEmpty())
            userReference.child("name").setValue(user.name)
        if (user.email.isNotEmpty())
            userReference.child("email").setValue(user.email)
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
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("doctors").child(doctorId)
                .child("prescriptions").child(imageId)
            prescriptionReference.child("url").setValue(imageLink)
            prescriptionReference.child("status").setValue(Status.InProgress)
            prescriptionReference.child("date").setValue(getTodaysDate())
        }
    }

}