package com.app.heal.model

import java.io.Serializable

class LatLng(var latitude: Double, var longitude: Double): Serializable {

    constructor() : this(0.0, 0.0)
}