package com.app.heal.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.model.Doctor
import com.app.heal.model.Status
import com.app.heal.utils.Util
import com.app.heal.utils.openPrescriptionActivity
import kotlinx.android.synthetic.main.item_doctor_detail_card.view.*

class DoctorsDetailsAdapter(context: Context) : RecyclerView.Adapter<DoctorsDetailsAdapter.DoctorsViewHolder>() {

    val mContext = context
    var doctors = arrayListOf<Doctor>()

    class DoctorsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsViewHolder {
        return DoctorsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_detail_card, parent, false)
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
        val prescription = doctor.prescription
        if (prescription != null) {
            val prescriptionNode = prescription.values?.toTypedArray()?.get(0)
            holder.view.prescriptionStatus.text = if (prescriptionNode.status.toString().isNotEmpty()) prescriptionNode.status.toString() else "Not Present"
            if (prescriptionNode.status != Status.Valid && prescriptionNode.status != Status.InProgress) {
                holder.view.doctorCard.setCardBackgroundColor(Color.parseColor("#FDC0BF"))
                holder.view.setOnClickListener {
                    mContext.openPrescriptionActivity(doctor.doctorId, Util.TASK_TYPE_SAME)
                }
            }
        } else {
            holder.view.prescriptionStatus.text = "Not Present"
            holder.view.doctorCard.setCardBackgroundColor(Color.parseColor("#FDC0BF"))
            holder.view.setOnClickListener {
                mContext.openPrescriptionActivity(doctor.doctorId, Util.TASK_TYPE_SAME)
            }
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