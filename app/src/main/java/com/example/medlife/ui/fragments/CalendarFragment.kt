package com.example.medlife.ui.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.models.MedicationDate
import com.example.medlife.ui.adapters.CalendarRecyclerAdapter

class CalendarFragment : Fragment(), View.OnClickListener {

    private lateinit var addForDayBtn   : Button
    private lateinit var adapter        : CalendarRecyclerAdapter

    private val datesList = arrayListOf<MedicationDate>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_calendar, container, false)

        addForDayBtn = view.findViewById(R.id.add_for_day_btn)

        adapter = CalendarRecyclerAdapter(view.context, datesList)
        adapter.setCalendarAdapterListener(object : CalendarRecyclerAdapter.CalendarAdapterListener{
            override fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
                getData(year, month, dayOfMonth)
            }
            override fun onItemClick(position: Int) {
                TODO("Not yet implemented")
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.medications_for_day_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        addForDayBtn.setOnClickListener(this)

        val calendar = Calendar.getInstance()
        getData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        return view
    }

    override fun onClick(v: View?) {

    }

    private fun getData(year : Int, month : Int, dayOfMonth : Int) {
        datesList.clear()

        datesList.add(MedicationDate())
        adapter.notifyDataSetChanged()
    }

}