package com.example.medlife.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.medlife.R
import com.example.medlife.models.MedicationDto

internal class MedicationDtoRecyclerAdapter(private val context : Context, private val dataSet: ArrayList<MedicationDto>) :
    RecyclerView.Adapter<MedicationDtoRecyclerAdapter.ViewHolder>() {
        internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val nameView            : TextView
            val iconView            : ImageView
            val dosageView          : TextView
            val frequencyView       : TextView
            val maxTakingDaysView   : TextView

            init {
                iconView            = view.findViewById(R.id.medication_icon)
                nameView            = view.findViewById(R.id.medication_name_text)
                dosageView          = view.findViewById(R.id.medication_dosage_text)
                frequencyView       = view.findViewById(R.id.medication_frequency_text)
                maxTakingDaysView   = view.findViewById(R.id.max_taking_days_text)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.medication_row, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameView.text            = dataSet[position].Name
        viewHolder.dosageView.text          = dataSet[position].Dosage
        viewHolder.frequencyView.text       = dataSet[position].TakingFrequency
        viewHolder.maxTakingDaysView.text   = dataSet[position].MaxTakingDays.toString()

        Glide.with(context).load(R.drawable.pill_default_image)
            .error(R.drawable.pill_default_image)
            .transform(CenterCrop(), CircleCrop())
            .into(viewHolder.iconView)
    }

    override fun getItemCount() = dataSet.size
}