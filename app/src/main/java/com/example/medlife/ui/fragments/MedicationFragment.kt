package com.example.medlife.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var medicationsAdapter : MedicationsRecyclerAdapter
    private lateinit var addMedicationBtn   : FloatingActionButton

    private val medicationsList : ArrayList<Medication> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_medication, container, false)

        medicationsAdapter = MedicationsRecyclerAdapter(requireActivity(), medicationsList, true)
        medicationsAdapter.setOnItemClickListener(object : MedicationsRecyclerAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                activity?.let{
                    (activity as MainActivity).goToAddEditMedication(medicationsList[position])
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
        recyclerView.layoutManager      = GridLayoutManager(activity, 2)
        recyclerView.adapter            = medicationsAdapter

        getMedications()

        return view
    }

    fun getMedications(){
        Thread {
            medicationsList.clear()
            if(activity != null){
                medicationsList.addAll(ApplicationDb.getInstance(activity as Context)!!.medicationDao().getAll())
                activity?.runOnUiThread { medicationsAdapter.notifyDataSetChanged() }
            }
        }.start()
    }

    fun getList() : ArrayList<Medication> = medicationsList
}