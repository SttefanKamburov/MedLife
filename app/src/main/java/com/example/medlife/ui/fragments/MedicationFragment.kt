package com.example.medlife.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.MainActivity
import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter

class MedicationFragment : Fragment() {

    private val medicationsList : ArrayList<Medication> = arrayListOf()

    private lateinit var medicationsAdapter: MedicationDtoRecyclerAdapter
    private lateinit var addMedicationBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_medication, container, false)

        medicationsAdapter = MedicationDtoRecyclerAdapter(view.context, medicationsList)
        medicationsAdapter.setOnItemClickListener(object  : MedicationDtoRecyclerAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                activity?.let{
                    (activity as MainActivity).goToAddEditMedication(medicationsList[position])
                }
            }
        })
        addMedicationBtn                = view.findViewById(R.id.add_medication_btn)
        addMedicationBtn.setOnClickListener {
            activity?.let {
                (activity as MainActivity).goToAddEditMedication(null)
            }
        }

        val recyclerView: RecyclerView  = view.findViewById(R.id.medication_fragment_recycler_view)
        recyclerView.layoutManager      = LinearLayoutManager(activity)
        recyclerView.adapter            = medicationsAdapter

        getMedications()

        return view
    }

    fun getMedications(){
        Thread {
            medicationsList.clear()
            if(activity != null){
                medicationsList.addAll(ApplicationDb.getInstance(activity as Context)!!.medicationDao().getAll())
                activity?.runOnUiThread {
                    addMedicationBtn.visibility = if( medicationsList.size > 0 ) View.GONE else View.VISIBLE
                    medicationsAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }
}