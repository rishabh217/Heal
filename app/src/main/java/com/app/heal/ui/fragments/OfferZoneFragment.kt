package com.app.heal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.heal.R
import com.app.heal.interfaces.UserPointsCallback

class OfferZoneFragment : BaseFragment(), UserPointsCallback {

    private lateinit var tvPoints: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_offer_zone, container, false)

        tvPoints = view.findViewById(R.id.points)

        firebaseManager.fetchUserPoints(this)

        return view
    }

    override fun onGetUserPoints(points: Double) {
        tvPoints.text = if (points >= 0.0) points.toString() else "0"
    }

}