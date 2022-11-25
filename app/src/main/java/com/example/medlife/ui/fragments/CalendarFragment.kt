package com.example.medlife.ui.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.models.Reminder
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.activities.MainActivity
import com.example.medlife.ui.adapters.CalendarRecyclerAdapter

class CalendarFragment : Fragment(), View.OnClickListener {

    private lateinit var addForDayBtn   : Button
    private lateinit var adapter        : CalendarRecyclerAdapter
    private lateinit var noDataText     : TextView

    private var selectedYear            : Int = 0
    private var selectedMonth           : Int = 0
    private var selectedDay             : Int = 0

    private val reminders = arrayListOf<Reminder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_calendar, container, false)

        addForDayBtn = view.findViewById(R.id.add_for_day_btn)
        noDataText   = view.findViewById(R.id.no_data_found_text)

        adapter = CalendarRecyclerAdapter(view.context, reminders)
        adapter.setCalendarAdapterListener(object : CalendarRecyclerAdapter.CalendarAdapterListener{
            override fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
                selectedYear    = year
                selectedMonth   = month
                selectedDay     = dayOfMonth
                getReminders()
            }
            override fun onItemClick(position: Int) {
                activity?.let{
                    (activity as MainActivity).goToAddEditReminder(reminders[position], null)
                }
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.medications_for_day_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        addForDayBtn.setOnClickListener(this)

        val calendar = Calendar.getInstance()
        selectedYear  = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedDay   = calendar.get(Calendar.DAY_OF_MONTH)
        getReminders()

        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == addForDayBtn.id && activity != null)
            (activity as MainActivity).showBottomSheet()
    }

    fun getReminders() {
        Thread {
            reminders.clear()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR          , selectedYear)
            calendar.set(Calendar.MONTH         , selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH  , selectedDay)

            val db = ApplicationDb.getInstance(requireActivity().applicationContext)

            val temp = db!!.reminderDao().loadByDate(calendar.timeInMillis)

            for(reminder in temp){
                val medication = db!!.medicationDao().loadById(reminder.medicationId)
                reminder.medicationName = medication.name
                reminder.medicationImage = medication.icon
                reminder.timesList.clear()
                reminder.timesList.addAll(db!!.reminderTimeDao().loadByReminderId(reminder.id))

                reminders.add(reminder)
            }

            reminders.sortedWith(compareBy<Reminder>{it.timesList[0].hour}.thenBy{it.timesList[0].minute})
            reminders.add(0, Reminder())

            requireActivity().runOnUiThread {
                noDataText.visibility = if(reminders.size > 1) View.GONE else View.VISIBLE
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

}