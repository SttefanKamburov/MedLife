package Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import models.MedicationDto

internal class MedicationDtoRecyclerAdapter(private val dataSet: Array<MedicationDto>) :
    RecyclerView.Adapter<MedicationDtoRecyclerAdapter.ViewHolder>()
{
        internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val nameView : TextView
            val iconView : ImageView
            val dosageView : TextView
            val frequencyView : TextView
            val maxTakingDaysView : TextView

            init {
                nameView = view.findViewById(R.id.medication_recycler_xml_name)
                iconView = view.findViewById(R.id.medication_recycler_xml_icon)
                dosageView = view.findViewById(R.id.medication_recycler_xml_dosage)
                frequencyView = view.findViewById(R.id.medication_recycler_xml_frequency)
                maxTakingDaysView = view.findViewById(R.id.medication_recycler_xml_max_taking_days)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medication_recycler, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.nameView.text = dataSet[position].Name
        //viewHolder.iconView. = dataSet[position].Name
        viewHolder.dosageView.text = dataSet[position].Dosage
        viewHolder.frequencyView.text = dataSet[position].TakingFrequency
        viewHolder.maxTakingDaysView.text = dataSet[position].MaxTakingDays.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}