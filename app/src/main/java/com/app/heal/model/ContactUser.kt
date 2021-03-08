package com.app.heal.model

class ContactUser(
    var name: String,
    var phone: String,
    var email: String
) {
    constructor() : this("", "", "")
}