package com.example.medlife.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import kotlin.Int

internal class MedicationsInfoRecyclerAdapter(
    private val context : Context,
    private val dataSet: ArrayList<Medication>) :
    RecyclerView.Adapter<MedicationsInfoRecyclerAdapter.ViewHolder>() {

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var iconView            : ImageView
        var nameText            : TextView
        var dosageText          : TextView
        var frequencyText       : TextView
        var maxTakingDaysText   : TextView

        init {
            iconView            = view.findViewById(R.id.medication_icon)
            nameText            = view.findViewById(R.id.name_text)
            dosageText          = view.findViewById(R.id.dosage_text)
            frequencyText       = view.findViewById(R.id.frequency_text)
            maxTakingDaysText   = view.findViewById(R.id.max_taking_days_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.medication_info_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameText.text            = dataSet[position].name
        viewHolder.dosageText.text          = dataSet[position].dosage
        viewHolder.frequencyText.text       = dataSet[position].takingFrequency
        viewHolder.maxTakingDaysText.text   = dataSet[position].maxTakingDays.toString()

        Utils.setImage(context, viewHolder.iconView, dataSet[position].icon, CircleCrop())
    }

    override fun getItemCount() = dataSet.size
}