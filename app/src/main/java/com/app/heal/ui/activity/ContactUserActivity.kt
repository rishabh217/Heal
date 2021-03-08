package com.app.heal.ui.activity

import android.os.Bundle
import com.app.heal.R
import com.app.heal.model.ContactUser
import com.app.heal.model.FlagStatus
import com.app.heal.utils.animateView
import com.app.heal.utils.getSelfUId
import com.app.heal.utils.setSnackBar
import kotlinx.android.synthetic.main.activity_contact_user.*

class ContactUserActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_user)

        submit?.setOnClickListener {
            animateView(submit)
            if (name.text.isEmpty() || phone.text.isEmpty()) {
                setSnackBar(findViewById(android.R.id.content), "Enter required details")
                return@setOnClickListener
            }
            val contact = ContactUser()
            contact.name = name.text.toString()
            contact.email = email.text.toString()
            contact.phone = phone.text.toString()
            contact.flag = FlagStatus.Open
            firebaseManager.setContactUserDetails(getSelfUId(), contact)
            setSnackBar(findViewById(android.R.id.content), "Request submitted successfully")
            this.finish()
        }

    }
}