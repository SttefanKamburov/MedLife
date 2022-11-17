package com.example.medlife

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.repository.MedicationDao
import com.example.medlife.ui.MedicationEditActivity
import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationFragment : Fragment() {

    private val medicationsList : ArrayList<Medication> = arrayListOf()
    private lateinit var medicationsAdapter: MedicationDtoRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_medication, container, false)

        medicationsAdapter = MedicationDtoRecyclerAdapter(view.context, medicationsList)
        medicationsAdapter.setOnItemClickListener(object  : MedicationDtoRecyclerAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                activity?.let{
                    val intent = Intent(requireContext(), MedicationEditActivity::class.java)
                    intent.putExtra("medication", medicationsList[position])
                    it.startActivity(intent)
                }
                getMedications(view.context)
            }
        })
        val recyclerView: RecyclerView  = view.findViewById(R.id.medication_fragment_recycler_view)
        recyclerView.layoutManager      = LinearLayoutManager(activity)
        recyclerView.adapter            = medicationsAdapter

        getMedications(view.context)

        return view
    }

    private fun getMedications(context: Context){
        this.lifecycleScope.launch(context = Dispatchers.IO){
            medicationsList.clear()
            medicationsList.addAll(ApplicationDb.getInstance(context)!!.medicationDao().getAll())
        }
        medicationsAdapter.notifyDataSetChanged()
    }
}