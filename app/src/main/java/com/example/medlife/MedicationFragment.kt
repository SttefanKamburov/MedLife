package com.example.medlife

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.models.MedicationDto
import com.example.medlife.repository.MedicationRepository
import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [medication.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedicationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val medicationsList : ArrayList<MedicationDto> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMedications()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(com.example.medlife.R.layout.fragment_medication, container, false)

        getMedications()

        val recyclerView: RecyclerView  = view.findViewById(com.example.medlife.R.id.medication_fragment_recycler_view)
        recyclerView.layoutManager      = LinearLayoutManager(view.context)
        recyclerView.adapter            = MedicationDtoRecyclerAdapter(view.context, medicationsList)

        return view
    }

    private fun getMedications(){
        medicationsList.clear()
        medicationsList.addAll(MedicationRepository.getMedicationArray())
    }
}