package com.app.heal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.app.heal.R

class DisplayInformationAdapter : PagerAdapter() {

    private var information = ArrayList<String?>()

    override fun getCount(): Int {
        return information.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    //To reload the pager everytime
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val informationLayout: View = LayoutInflater.from(container?.context).inflate(R.layout.item_information_card, container, false)!!

        val infoText: TextView = informationLayout.findViewById(R.id.infoText)
        val info = information[position]
        if (info != null)
            infoText.text = info

        container.addView(informationLayout)

        return informationLayout
    }

    fun setInformation(information: ArrayList<String?>) {
        this.information = information
        notifyDataSetChanged()
    }
}