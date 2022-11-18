package com.example.medlife.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.medlife.R
import com.example.medlife.models.Medication
import java.lang.String
import kotlin.Int


internal class MedicationDtoRecyclerAdapter(private val context : Context, private val dataSet: ArrayList<Medication>) :
    RecyclerView.Adapter<MedicationDtoRecyclerAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    internal inner class ViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view){
            val nameView            : TextView
            val iconView            : ImageView

            init {
                iconView            = view.findViewById(R.id.medication_icon)
                nameView            = view.findViewById(R.id.medication_name_text)

                view.setOnClickListener{
                    listener.onItemClick(adapterPosition)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.medication_row, parent, false);

        return  ViewHolder(view, mListener);
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameView.text            = dataSet[position].Name

        Glide.with(context).load(R.drawable.pill_default_image)
            .error(R.drawable.pill_default_image)
            .transform(CenterCrop(), CircleCrop())
            .into(viewHolder.iconView)
    }

    override fun getItemCount() = dataSet.size
}