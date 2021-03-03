package com.app.heal.utils

import android.content.SharedPreferences
import com.app.heal.model.LatLng
import javax.inject.Inject

class AppPreferences @Inject constructor(var preferences: SharedPreferences) {

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var lastLocation: LatLng
        get() = LatLng(
            preferences.getString("latitude", "0")!!.toDouble(),
            preferences.getString("longitude", "0")!!.toDouble()
        )
        set(value) = preferences.edit {
            it.putString("latitude", value.latitude.toString())
            it.putString("longitude", value.longitude.toString())

        }

}