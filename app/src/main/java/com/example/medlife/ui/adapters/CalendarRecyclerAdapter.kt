package com.example.medlife.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.MedicationDate
import java.util.*
import kotlin.Int
import kotlin.collections.ArrayList

internal class CalendarRecyclerAdapter(
    private val context : Context,
    private val dataSet: ArrayList<MedicationDate>) :
    RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder>() {

    companion object{
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM   = 1
        private const val VIEW_TYPE_FOOTER = 2
    }

    private lateinit var listener : CalendarAdapterListener

    interface CalendarAdapterListener{
        fun onDateSelected(year : Int, month : Int, dayOfMonth : Int)
        fun onItemClick(position: Int)
    }

    fun setCalendarAdapterListener(listener: CalendarAdapterListener) {
        this.listener = listener
    }

    internal inner class ViewHolder(view: View, viewType : Int) : RecyclerView.ViewHolder(view){

        var calendar            : CalendarView? = null
        var title               : TextView?     = null

        var container           : View?         = null
        var nameView            : TextView?     = null
        var iconView            : ImageView?    = null

        init {
            if(viewType != VIEW_TYPE_FOOTER){
                if(viewType == VIEW_TYPE_HEADER) {
                    calendar    = itemView.findViewById(R.id.calendar_view)
                    title       = itemView.findViewById(R.id.reminders_title)

                    setTitle(title, Utils.convertTimestampToDate(Calendar.getInstance().timeInMillis))

                    calendar?.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        listener.onDateSelected(
                            year,
                            month,
                            dayOfMonth
                        )
                        val displayMonth = month + 1
                        setTitle(title, "$dayOfMonth/$displayMonth/$year")
                    }
                }
                else{
                    container   = view.findViewById(R.id.container)
                    iconView    = view.findViewById(R.id.medication_icon)
                    nameView    = view.findViewById(R.id.medication_name_text)

                    container?.setOnClickListener{
                        listener.onItemClick(adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout : Int = when (viewType) {
            VIEW_TYPE_HEADER -> R.layout.calendar_header
            VIEW_TYPE_FOOTER -> R.layout.emtpy_footer
            else -> R.layout.medication_for_day_item
        }

        return ViewHolder(LayoutInflater.from(context).inflate(layout, parent, false), viewType)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_ITEM){

        }
    }

    private fun setTitle(title : TextView?, date : String){
        title?.text = context.getString(R.string.reminders_for, date)
    }

    override fun getItemCount() = dataSet.size + 1

    override fun getItemViewType(position: Int): Int {
        if(position == itemCount - 1)
            return VIEW_TYPE_FOOTER
        else if (position == 0)
            return VIEW_TYPE_HEADER
        return VIEW_TYPE_ITEM
    }
}