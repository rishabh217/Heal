package com.app.heal.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.adapter.DoctorsDetailsAdapter
import com.app.heal.interfaces.UserDetailsCallback
import com.app.heal.model.Doctor
import com.app.heal.model.Gender
import com.app.heal.model.User
import com.app.heal.utils.animateView
import com.app.heal.utils.getSelfUId
import com.app.heal.utils.logout
import com.app.heal.utils.setSnackBar

class SelfDetailsFragment : BaseFragment(), UserDetailsCallback {

    private lateinit var rvDoctors: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var doctorsAdapter: DoctorsDetailsAdapter

    private lateinit var avatar: ImageView
    private lateinit var avatarLayout: CardView
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var doctorsCard: CardView
    private lateinit var progressBar: ProgressBar

    private lateinit var share: Button
    private lateinit var logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_self_details, container, false)

        avatar = view.findViewById(R.id.avatar)
        avatarLayout = view.findViewById(R.id.avatarLayout)
        name = view.findViewById(R.id.name)
        age = view.findViewById(R.id.age)
        doctorsCard = view.findViewById(R.id.doctorsCard)
        progressBar = view.findViewById(R.id.progressBar)

        share = view.findViewById(R.id.share)
        logout = view.findViewById(R.id.logout)

        rvDoctors = view.findViewById(R.id.rvDoctors)
        linearLayoutManager = LinearLayoutManager(this.context)
        doctorsAdapter = DoctorsDetailsAdapter(this.context!!)
        rvDoctors.adapter = doctorsAdapter
        rvDoctors.layoutManager = linearLayoutManager

        firebaseManager.getUserDetails(getSelfUId(), this)

        share.setOnClickListener { animateView(share) }

        logout.setOnClickListener {
            animateView(logout)
            if (this.context != null)
                this.context!!.logout()
        }

        return view
    }

    override fun onGetUserDetails(user: User?) {
        if (user != null) {
            progressBar.visibility = View.GONE

            name.text = user.name.toString()
            age.text = user.age.toString()
            when (user.gender) {
                Gender.Male -> {
                    avatar.setImageResource(R.drawable.ic_male_avatar)
                    avatarLayout.setCardBackgroundColor(Color.parseColor("#29B6F6"))
                }
                Gender.Female -> {
                    avatar.setImageResource(R.drawable.ic_female_avatar)
                    avatarLayout.setCardBackgroundColor(Color.parseColor("#EC407A"))
                }
                else -> {
                    setSnackBar(view?.findViewById(android.R.id.content), "Gender is not updated")
                }
            }

            val doctors = user.doctors
            if (doctors != null) {
                val doctorsArr = arrayListOf<Doctor>()
                for (doctor in doctors) {
                    doctorsArr.add(doctor.value)
                }
                doctorsAdapter.setDoctorsList(doctorsArr)
            } else
                doctorsCard.visibility = View.GONE
        }
    }

}