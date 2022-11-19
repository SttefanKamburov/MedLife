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

internal class MedicationsRecyclerAdapter(
    private val context : Context,
    private val dataSet: ArrayList<Medication>,
    private val isGrid: Boolean) :
    RecyclerView.Adapter<MedicationsRecyclerAdapter.ViewHolder>() {

    private lateinit var listener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private var container   : View
        var nameView            : TextView
        var iconView            : ImageView

        init {
            container   = view.findViewById(R.id.container)
            iconView    = view.findViewById(R.id.medication_icon)
            nameView    = view.findViewById(R.id.medication_name_text)

            container.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context)
            .inflate(if(isGrid) R.layout.medication_grid_item else R.layout.medication_item
                , parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nameView.text = dataSet[position].Name
        Utils.setImage(context, viewHolder.iconView, dataSet[position].Icon, CircleCrop())
    }

    override fun getItemCount() = dataSet.size
}