package com.app.heal.ui.fragments.prescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.app.heal.R
import com.app.heal.interfaces.FirstDoctorIdCallback
import com.app.heal.ui.fragments.BaseFragment
import com.app.heal.utils.Util
import com.app.heal.utils.animateView
import com.app.heal.utils.openPrescriptionActivity

class PrescriptionInvalidFragment : BaseFragment(), FirstDoctorIdCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prescription_invalid, container, false)

        val uploadButton = view?.findViewById<Button>(R.id.uploadAgain)
        uploadButton?.setOnClickListener {
            animateView(uploadButton)
            firebaseManager.getFirstDoctorId(this)
        }

        return view
    }

    override fun onGetFirstDoctorId(doctorId: String?) {
        if (this.context != null && !doctorId.isNullOrEmpty()) {
            this.context!!.openPrescriptionActivity(doctorId, Util.TASK_TYPE_SAME)
        }
    }

}