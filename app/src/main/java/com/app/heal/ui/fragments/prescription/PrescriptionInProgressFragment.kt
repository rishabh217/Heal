package com.app.heal.ui.fragments.prescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.heal.R
import com.app.heal.adapter.DisplayInformationAdapter
import com.app.heal.interfaces.DisplayInformationCallback
import com.app.heal.ui.CirclePageIndicator
import com.app.heal.ui.fragments.BaseFragment
import com.app.heal.utils.Util
import com.app.heal.utils.autoScroll

class PrescriptionInProgressFragment : BaseFragment(), DisplayInformationCallback {

    private lateinit var vpInformation: ViewPager
    private lateinit var indicatorInformation: CirclePageIndicator
    private lateinit var informationAdapter: DisplayInformationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prescription_in_progress, container, false)

        vpInformation = view.findViewById(R.id.vpTips)
        indicatorInformation = view.findViewById(R.id.indicatorTips)
        informationAdapter = DisplayInformationAdapter()
        vpInformation.adapter = informationAdapter
        indicatorInformation.setViewPager(vpInformation)
        vpInformation.offscreenPageLimit = 5
        vpInformation.autoScroll(5000)

        firebaseManager.getDisplayInformation(Util.INFORMATION_TYPE_TIPS, this)

        return view
    }

    override fun onGetInformation(info: ArrayList<String?>) {
        informationAdapter.setInformation(info)
    }

}