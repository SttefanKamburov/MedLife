package com.example.medlife

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.models.MedicationDto
import com.example.medlife.repository.MedicationRepository
import com.example.medlife.ui.MedicationEditActivity
import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter
import kotlinx.coroutines.Dispatchers.Main

class MedicationFragment : Fragment() {

    private val medicationsList : ArrayList<MedicationDto> = arrayListOf()
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
                    val intent = Intent (it, MedicationEditActivity::class.java)
                    it.startActivity(intent)
                }
            }
        })
        val recyclerView: RecyclerView  = view.findViewById(R.id.medication_fragment_recycler_view)
        recyclerView.layoutManager      = LinearLayoutManager(activity)
        recyclerView.adapter            = medicationsAdapter

        getMedications()

        return view
    }

    private fun getMedications(){
        medicationsList.clear()
        medicationsList.addAll(MedicationRepository.getMedicationArray())
        medicationsAdapter.notifyDataSetChanged()
    }
}