package com.app.heal.model

class Dose(
    var doseMap: HashMap<Long, MedStatus>? // Time -> Boolean if dose taken or not
) {
    constructor() : this(null)
}