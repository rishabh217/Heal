package com.app.heal.interfaces

interface ImageUploadStatusCallback {
    fun onImageUploaded(status: Boolean, imageUrl: String)
}