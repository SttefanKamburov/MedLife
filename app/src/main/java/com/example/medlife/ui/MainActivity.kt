package com.example.medlife.ui

import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.models.MedicationDto
import com.example.medlife.repository.MedicationRepository

class MainActivity : AppCompatActivity() {

    private lateinit var medicationDtoRecyclerAdapter: MedicationDtoRecyclerAdapter
    private val medicationsList : ArrayList<MedicationDto> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);

        init()
        getMedications()
    }

    private fun init(){
        medicationDtoRecyclerAdapter    = MedicationDtoRecyclerAdapter(this, medicationsList)

        val recyclerView: RecyclerView  = findViewById(R.id.medication_recycler_view)
        recyclerView.layoutManager      = LinearLayoutManager(this)
        recyclerView.adapter            = medicationDtoRecyclerAdapter
    }

    private fun getMedications(){
        // TODO
        medicationsList.clear()
        medicationsList.addAll(MedicationRepository.getMedicationArray())
        medicationDtoRecyclerAdapter.notifyDataSetChanged()
    }
}