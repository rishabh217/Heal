package com.app.heal.ui.activity

import android.os.Bundle
import android.view.View
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
import com.google.android.material.tabs.TabLayout

class HomeActivity : BaseActivity(), PrescriptionDetailsCallback, FirstDoctorIdCallback {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var fragmentType: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewPager = findViewById<View>(R.id.viewPager) as ViewPager
        tabLayout = findViewById<TabLayout>(R.id.tabs)

        firebaseManager.getFirstDoctorId(this)

    }

    private fun setupViewPagerAndTabs() {
        viewPager.adapter = HomePagerAdapter(this, supportFragmentManager, fragmentType)
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.currentItem = 1
    }

    override fun onGetPrescriptionDetails(prescription: Prescription?) {
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