package com.app.heal.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.adapter.DoctorsAdapter
import com.app.heal.interfaces.UserDetailsCallback
import com.app.heal.model.Doctor
import com.app.heal.model.User
import com.app.heal.ui.activity.AddDoctorActivity
import com.app.heal.utils.animateView
import com.app.heal.utils.getSelfUId
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DoctorsFragment : BaseFragment(), UserDetailsCallback {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var doctorsAdapter: DoctorsAdapter

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctors, container, false)

        progressBar = view.findViewById(R.id.progressDoctors)

        rvDoctors = view.findViewById(R.id.rvDoctors)
        gridLayoutManager = GridLayoutManager(this.context, 2)
        doctorsAdapter = DoctorsAdapter(this.context!!)
        rvDoctors.adapter = doctorsAdapter
        rvDoctors.layoutManager = gridLayoutManager

        firebaseManager.getUserDetails(getSelfUId(), this)

        val fab = view.findViewById<FloatingActionButton>(R.id.addDocButton)
        fab.bringToFront()
        fab.setOnClickListener {
            animateView(fab)
            startActivity(Intent(this.context, AddDoctorActivity::class.java))
        }

        return view
    }

    override fun onGetUserDetails(user: User?) {
        if (user != null) {
            progressBar.visibility = View.GONE
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