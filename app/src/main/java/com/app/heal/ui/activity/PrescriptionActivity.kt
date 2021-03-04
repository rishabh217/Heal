package com.app.heal.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.heal.R
import com.app.heal.interfaces.ImageUploadStatusCallback
import com.app.heal.utils.animateView
import com.app.heal.utils.setSnackBar
import kotlinx.android.synthetic.main.activity_prescription.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class PrescriptionActivity : BaseActivity(), ImageUploadStatusCallback {

    private val PICK_IMAGE_REQUEST = 1001
    private val STORAGE_PERMISSION_CODE = 1

    private lateinit var filePath: Uri
    private lateinit var imageView: ImageView

    private var doctorId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            doctorId = bundle.getString("doctorId")!!
        }

        imageView = findViewById(R.id.imagePrescription)
        imageView.setOnClickListener { askStoragePermission() }

        savePrescription?.isEnabled = false
        savePrescription?.setOnClickListener {
            animateView(savePrescription)
            if (doctorId.isNotEmpty())
                firebaseManager.uploadPrescription(this, filePath, doctorId, this)
            else
                setSnackBar(findViewById(android.R.id.content), "Some Problem Occurred")
        }

    }

    private fun askStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23 ||
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
            chooseImage(PICK_IMAGE_REQUEST, imageView)
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
        ) {
            AlertDialog.Builder(this)
                    .setTitle("Storage Permission needed")
                    .setMessage("Storage Permission is needed to upload image!!")
                    .setPositiveButton("Request",
                            DialogInterface.OnClickListener { dialog, which ->
                                ActivityCompat.requestPermissions(
                                        this,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        STORAGE_PERMISSION_CODE
                                )
                            })
                    .setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage(PICK_IMAGE_REQUEST, imageView)
            } else {
                setSnackBar(findViewById(android.R.id.content), "Storage Permission is Required")
            }
        }
    }

    private fun chooseImage(imageRequest: Int, imageView: ImageView) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        this.imageView = imageView
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(data?.data != null) {
                filePath = data?.data!!
                when (requestCode) {
                    PICK_IMAGE_REQUEST -> {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                            val bos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
                            imageView.setImageBitmap(bitmap)
                            addImage?.visibility = View.GONE
                            imageView.isEnabled = false
                            savePrescription?.isEnabled = true
                            savePrescription?.setBackgroundColor(Color.parseColor("#D81B60"))
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onImageUploaded(status: Boolean, imageUrl: String) {
        if (status) {
            setSnackBar(findViewById(android.R.id.content), "Uploaded Successfully")
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

}