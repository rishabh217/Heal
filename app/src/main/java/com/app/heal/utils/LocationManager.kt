package com.app.heal.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.heal.model.LatLng
import com.google.android.gms.location.*
import javax.inject.Inject

class LocationManager @Inject constructor(var appPreferences: AppPreferences, var firebaseManager: FirebaseManager) {

    var mActivity: Activity? = null
    var mFamId: String? = null

    @SuppressLint("MissingPermission")
    fun getLastLocation(activity: Activity, famId: String) {
        this.mActivity = activity
        this.mFamId = famId
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (checkPermissions(activity)) {
            if (isLocationEnabled(activity)) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(activity) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData(activity, mFusedLocationClient)
                    } else {
                        uploadLocation(location)
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivity(intent)
            }
        } else {
            requestPermissions(activity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(activity: Activity, mFusedLocationClient: FusedLocationProviderClient) {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            uploadLocation(mLastLocation)
        }
    }

    private fun isLocationEnabled(activity: Activity): Boolean {
        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions(activity: Activity) {

        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Util.PERMISSION_ID
        )
    }

    fun uploadLocation(location: Location) {

        val latlng: LatLng = LatLng(location.latitude, location.longitude)
        appPreferences.lastLocation = latlng
        if (!mFamId.isNullOrEmpty())
//            firebaseManager.uploadLocation(latlng, mFamId!!)
        if (mActivity != null)
            Toast.makeText(mActivity, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()

    }
}