package com.app.heal.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.app.heal.R
import com.app.heal.adapter.HomePagerAdapter
import com.app.heal.interfaces.FirstDoctorIdCallback
import com.app.heal.interfaces.PrescriptionDetailsCallback
import com.app.heal.model.Prescription
import com.app.heal.model.Status
import com.app.heal.ui.fragments.DoctorsFragment
import com.app.heal.ui.fragments.prescription.PrescriptionInvalidFragment
import com.app.heal.ui.fragments.prescription.PrescriptionInProgressFragment
import com.app.heal.ui.fragments.prescription.PrescriptionIssueFragment
import com.app.heal.ui.fragments.prescription.PrescriptionNotUploadedFragment
import com.app.heal.utils.Util
import com.app.heal.utils.animateView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), PrescriptionDetailsCallback, FirstDoctorIdCallback {

    private lateinit var viewPager: ViewPager
    private lateinit var fragmentType: Fragment

    private lateinit var offers: FrameLayout
    private lateinit var doctors: FrameLayout
    private lateinit var profile: FrameLayout
    private lateinit var offersImage: ImageView
    private lateinit var doctorsImage: ImageView
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById<View>(R.id.viewPager) as ViewPager

        offers = findViewById(R.id.offers)
        offersImage = findViewById(R.id.offersImage)
        doctors = findViewById(R.id.doctors)
        doctorsImage = findViewById(R.id.doctorsImage)
        profile = findViewById(R.id.profile)
        profileImage = findViewById(R.id.profileImage)

        offers.setOnClickListener {
            selectOffersSection()
            viewPager.currentItem = 0
        }
        doctors.setOnClickListener {
            selectDoctorsSection()
            viewPager.currentItem = 1
        }
        profile.setOnClickListener {
            selectProfileSection()
            viewPager.currentItem = 2
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> selectOffersSection()
                    1 -> selectDoctorsSection()
                    2 -> selectProfileSection()
                }
            }

        })

        firebaseManager.getFirstDoctorId(this)

    }

    private fun selectOffersSection() {
        animateView(offersImage, doctorsImage, profileImage)
        offersImage.setImageResource(R.drawable.ic_offers_selected)
        doctorsImage.setImageResource(R.drawable.ic_doctors)
        profileImage.setImageResource(R.drawable.ic_profile)
    }

    private fun selectDoctorsSection() {
        animateView(offersImage, doctorsImage, profileImage)
        offersImage.setImageResource(R.drawable.ic_offers)
        doctorsImage.setImageResource(R.drawable.ic_doctors_selected)
        profileImage.setImageResource(R.drawable.ic_profile)
    }

    private fun selectProfileSection() {
        animateView(offersImage, doctorsImage, profileImage)
        offersImage.setImageResource(R.drawable.ic_offers)
        doctorsImage.setImageResource(R.drawable.ic_doctors)
        profileImage.setImageResource(R.drawable.ic_profile_selected)
    }

    private fun setupViewPagerAndTabs() {
        viewPager.adapter = HomePagerAdapter(this, supportFragmentManager, fragmentType)
        viewPager.offscreenPageLimit = 2
        viewPager.currentItem = 1
    }

    override fun onGetPrescriptionDetails(prescription: Prescription?) {
        progressBar?.visibility = View.GONE
        if (prescription != null) {
            fragmentType = when (prescription.status) {
                Status.InProgress -> {
                    PrescriptionInProgressFragment()
                }
                Status.Invalid -> {
                    PrescriptionInvalidFragment()
                }
                Status.Issue -> {
                    PrescriptionIssueFragment()
                }
                Status.Valid -> {
                    DoctorsFragment()
                }
                Status.NotUploaded -> {
                    PrescriptionNotUploadedFragment()
                }
            }
        }
        else {
            fragmentType = PrescriptionNotUploadedFragment()
        }
        setupViewPagerAndTabs()
    }

    override fun onGetFirstDoctorId(doctorId: String?) {
        if (doctorId != null) {
            firebaseManager.getPrescriptionDetails(doctorId, Util.LISTENER_TYPE_VALUE_EVENT, this)
        }
    }

}