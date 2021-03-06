package com.app.heal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.adapter.DoctorsAdapter
import com.app.heal.interfaces.UserDetailsCallback
import com.app.heal.model.Doctor
import com.app.heal.model.User
import com.app.heal.utils.getSelfUId

class DoctorsFragment : BaseFragment(), UserDetailsCallback {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var doctorsAdapter: DoctorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctors, container, false)

        rvDoctors = view.findViewById(R.id.rvDoctors)
        gridLayoutManager = GridLayoutManager(this.context, 2)
        doctorsAdapter = DoctorsAdapter()
        rvDoctors.adapter = doctorsAdapter
        rvDoctors.layoutManager = gridLayoutManager

        firebaseManager.getUserDetails(getSelfUId(), this)

        return view
    }

    override fun onGetUserDetails(user: User?) {
        if (user != null) {
            val doctors = user.doctors
            if (doctors != null) {
                val doctorsArr = arrayListOf<Doctor>()
                for (doctor in doctors) {
                    doctorsArr.add(doctor.value)
                }
                doctorsAdapter.setDoctorsList(doctorsArr)
            }
        }
    }

}