package com.example.medlife.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.activities.MainActivity
import com.example.medlife.ui.adapters.MedicationsRecyclerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MedicationFragment : Fragment() {

    private lateinit var searchEdt          : EditText
    private lateinit var noDataText         : TextView
    private lateinit var medicationsAdapter : MedicationsRecyclerAdapter
    private lateinit var addMedicationBtn   : FloatingActionButton

    private val medicationsList             : ArrayList<Medication> = arrayListOf()
    private val medicationsBufferList       : ArrayList<Medication> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_medication, container, false)

        searchEdt       = view.findViewById(R.id.search_edt)
        noDataText      = view.findViewById(R.id.no_data_found_text)

        medicationsAdapter = MedicationsRecyclerAdapter(requireActivity(), medicationsBufferList, true)
        medicationsAdapter.setOnItemClickListener(object : MedicationsRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                activity?.let{
                    (activity as MainActivity).goToAddEditMedication(medicationsBufferList[position])
                }
            }
        })
        addMedicationBtn = view.findViewById(R.id.add_medication_btn)
        addMedicationBtn.setOnClickListener {
            activity?.let {
                (activity as MainActivity).goToAddEditMedication(null)
            }
        }

        val recyclerView: RecyclerView  = view.findViewById(R.id.medication_fragment_recycler_view)
        recyclerView.layoutManager  = GridLayoutManager(activity, 2)
        recyclerView.adapter        = medicationsAdapter

        searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                refreshMedications(p0.toString())
            }
        })

        getMedications()

        return view
    }

    private fun refreshMedications(searchText : String){
        medicationsBufferList.clear()

        for (medication in medicationsList){
            if(medication.Name.lowercase().contains(searchText.lowercase())
                || medication.TakingFrequency.lowercase().contains(searchText.lowercase())
                || medication.Dosage.lowercase().contains(searchText.lowercase())
                || medication.MaxTakingDays.toString().lowercase().contains(searchText.lowercase()))
                medicationsBufferList.add(medication)
        }

        noDataText.visibility = if (medicationsBufferList.size > 0) View.GONE else View.VISIBLE
        medicationsAdapter.notifyDataSetChanged()
    }

    fun getMedications(){
        Thread {
            medicationsList.clear()
            if(activity != null){
                medicationsList.addAll(ApplicationDb.getInstance(activity as Context)!!.medicationDao().getAll())
                activity?.runOnUiThread { refreshMedications("") }
            }
        }.start()
    }

    fun getList() : ArrayList<Medication> = medicationsList
}