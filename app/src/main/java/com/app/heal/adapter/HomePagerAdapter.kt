package com.app.heal.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.heal.ui.fragments.DoctorsFragment
import com.app.heal.ui.fragments.OfferZoneFragment
import com.app.heal.ui.fragments.SelfDetailsFragment

class HomePagerAdapter internal constructor(private val mContext: Context, fm: FragmentManager, fragmentType: Fragment) : FragmentPagerAdapter(fm) {

    private val mFragmentType = fragmentType

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                OfferZoneFragment()
            }
            1 -> {
                mFragmentType
            }
            2 -> {
                SelfDetailsFragment()
            }
            else -> DoctorsFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Offers"
            }
            1 -> {
                "Doctors"
            }
            2 -> {
                "Profile"
            }
            else -> "Doctors"
        }
    }

}