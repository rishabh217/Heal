package com.app.heal.ui.fragments.prescription

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.app.heal.R
import com.app.heal.interfaces.FirstDoctorIdCallback
import com.app.heal.ui.activity.ContactUserActivity
import com.app.heal.ui.fragments.BaseFragment
import com.app.heal.utils.Util
import com.app.heal.utils.animateView
import com.app.heal.utils.openPrescriptionActivity

class PrescriptionIssueFragment : BaseFragment(), FirstDoctorIdCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prescription_issue, container, false)

        val uploadButton = view?.findViewById<Button>(R.id.uploadAgain)
        val contactUser = view?.findViewById<Button>(R.id.contactUser)

        uploadButton?.setOnClickListener {
            animateView(uploadButton)
            firebaseManager.getFirstDoctorId(this)
        }

        contactUser?.setOnClickListener {
            animateView(contactUser)
            startActivity(Intent(this.context!!, ContactUserActivity::class.java))
        }

        return view
    }

    override fun onGetFirstDoctorId(doctorId: String?) {
        if (this.context != null && !doctorId.isNullOrEmpty()) {
            this.context!!.openPrescriptionActivity(doctorId, Util.TASK_TYPE_SAME)
        }
    }

}