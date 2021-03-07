package com.app.heal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.heal.R
import com.app.heal.model.DailyDose
import com.app.heal.model.MedStatus
import com.app.heal.utils.animateView
import com.app.heal.utils.getDateString
import com.app.heal.utils.isTimeInAnHour
import kotlinx.android.synthetic.main.item_medicine_course_card.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MedicinesAdapter(callback: Callback) : RecyclerView.Adapter<MedicinesAdapter.MedicinesViewHolder>() {

    val mCallback = callback
    var medicines = arrayListOf<DailyDose>()

    class MedicinesViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicinesViewHolder {
        return MedicinesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_medicine_course_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MedicinesViewHolder, position: Int) {
        holder.view.medButtonsLayout.visibility = View.GONE
        val medicine = medicines[position]
        holder.view.medName.text = medicine.name
        val points = medicine.points
        val dose = medicine.dailyDoseMap
        if (dose != null) {
            val todaysDose = dose[getDateString(Date())]
            if (todaysDose != null) {
                val doseMap = todaysDose.doseMap
                if (doseMap != null) {
                    val doseCount = doseMap.size
                    holder.view.medDoses.text = "$doseCount doses"
                    val progressStep: Int = ((100.0 / doseCount)).toInt()
                    var takenCount = 0
                    for (doseTime in doseMap) {
                        val time = doseTime.key.toLong()
                        val status = doseTime.value
                        if (status == MedStatus.Taken)
                            takenCount += 1
                        if (isTimeInAnHour(time)) {
                            if (status != MedStatus.Taken) {
                                holder.view.medButtonsLayout.visibility = View.VISIBLE
                                val formatter = SimpleDateFormat("hh:mm aa")
                                holder.view.medTime.text = formatter.format(Date(time))
                                holder.view.medTaken.setOnClickListener {
                                    animateView(holder.view.medTaken)
                                    mCallback.onMedicineCardClick(
                                        (points.toDouble() / doseCount),
                                        time.toString(),
                                        medicine.medicineId,
                                        MedStatus.Taken
                                    )
                                }
                                holder.view.medMissed.setOnClickListener {
                                    animateView(holder.view.medMissed)
                                    mCallback.onMedicineCardClick(
                                        0.0,
                                        time.toString(),
                                        medicine.medicineId,
                                        MedStatus.NotTaken
                                    )
                                }
                            }
                        } else {
                            if (status == MedStatus.Upcoming && time < Date().time) {
                                mCallback.onMedicineCardClick(0.0, time.toString(), medicine.medicineId, MedStatus.Missed)
                            }
                        }
                    }
                    holder.view.dosesBar.progress = takenCount * progressStep
                    holder.view.medsProgress.text = "$takenCount / $doseCount"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    fun setMedicinesList(medicines: ArrayList<DailyDose>) {
        this.medicines = medicines
        notifyDataSetChanged()
    }

    interface Callback {
        fun onMedicineCardClick(points: Double, time: String, medicineId: String, status: MedStatus)
    }

}