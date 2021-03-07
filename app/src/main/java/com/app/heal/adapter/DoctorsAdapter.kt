package com.app.heal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.model.Doctor
import com.app.heal.utils.openDoctorActivity
import kotlinx.android.synthetic.main.item_doctor_card.view.*

class DoctorsAdapter(context: Context) : RecyclerView.Adapter<DoctorsAdapter.DoctorsViewHolder>() {

    val mContext = context
    var doctors = arrayListOf<Doctor>()

    class DoctorsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsViewHolder {
        return DoctorsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DoctorsViewHolder, position: Int) {
        holder.view.docName.visibility = View.VISIBLE
        holder.view.medsCount.visibility = View.VISIBLE
        val doctor = doctors[position]
        val docName = doctor.name
        if (docName.isNotEmpty())
            holder.view.docName.text = doctor.name
        else
            holder.view.docName.visibility = View.INVISIBLE
        val medicines = doctor.medicines
        if (medicines != null)
            holder.view.medsCount.text = "${medicines.size} Medicine"
        else
            holder.view.medsCount.visibility = View.INVISIBLE
        holder.view.setOnClickListener {
            mContext.openDoctorActivity(doctor.doctorId)
        }
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    fun setDoctorsList(doctors: ArrayList<Doctor>) {
        this.doctors = doctors
        notifyDataSetChanged()
    }

}