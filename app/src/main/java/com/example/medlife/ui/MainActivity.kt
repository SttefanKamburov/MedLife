package com.example.medlife.ui

import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.medlife.R
import com.example.medlife.models.MedicationDto
import com.example.medlife.repository.MedicationRepository
import com.example.medlife.ui.adapters.PageAdapter
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    //private lateinit var medicationDtoRecyclerAdapter: MedicationDtoRecyclerAdapter
    private val medicationsList : ArrayList<MedicationDto> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);

        val viewPager = findViewById<ViewPager>(R.id.main_activity_view_pager)
        viewPager.adapter = PageAdapter(supportFragmentManager)
        //init()
        //getMedications()
    }

    //private fun init(){
    //    medicationDtoRecyclerAdapter    = MedicationDtoRecyclerAdapter(this, medicationsList)
//
    //    val recyclerView: RecyclerView  = findViewById(R.id.medication_recycler_view)
    //    recyclerView.layoutManager      = LinearLayoutManager(this)
    //    recyclerView.adapter            = medicationDtoRecyclerAdapter
    //}
//
    //private fun getMedications(){
    //    // TODO
    //    medicationsList.clear()
    //    medicationsList.addAll(MedicationRepository.getMedicationArray())
    //    medicationDtoRecyclerAdapter.notifyDataSetChanged()
    //}
}