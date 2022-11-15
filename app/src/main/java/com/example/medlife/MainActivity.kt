package com.example.medlife

import Adapters.MedicationDtoRecyclerAdapter
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.databinding.MainActivityBinding
import com.google.android.flexbox.*
import models.MedicationDto
import repository.MedicationRepository

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: MainActivityBinding
    private lateinit var medicationDtoRecyclerAdapter: MedicationDtoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);


        val recyclerView: RecyclerView = findViewById(R.id.main_activity_recycler)
        medicationDtoRecyclerAdapter = MedicationDtoRecyclerAdapter(MedicationRepository.getMedicationArray())
        val layoutManager = FlexboxLayoutManager(baseContext).apply {
            alignItems = AlignItems.CENTER
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = medicationDtoRecyclerAdapter
        medicationDtoRecyclerAdapter.notifyDataSetChanged()
        //onCreatePopulateMedications()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
}