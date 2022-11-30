package com.example.medlife.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.adapters.MedicationsInfoRecyclerAdapter

class ReminderActivity : AppCompatActivity() {

    private lateinit var recyclerView   : RecyclerView
    private lateinit var adapter        : MedicationsInfoRecyclerAdapter

    private var medicationsList : ArrayList<Medication> = arrayListOf()
    private var medicationsIds  : ArrayList<Long> = arrayListOf()
    private var timestamp       : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        if(!intent.hasExtra(Utils.INTENT_TRANSFER_MEDICATIONS) || !intent.hasExtra(Utils.INTENT_TRANSFER_TIMESTAMP))
            finish()

        medicationsIds.addAll(intent.getParcelableArrayListExtra(Utils.INTENT_TRANSFER_MEDICATIONS)!!)
        timestamp = intent.getLongExtra(Utils.INTENT_TRANSFER_TIMESTAMP, 0)

        findViewById<ImageView>(R.id.back_arrow_image_view).visibility = View.INVISIBLE
        (findViewById<TextView>(R.id.toolbar_title_text_view)).text = getString(R.string.reminders)

        recyclerView = findViewById(R.id.medications_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MedicationsInfoRecyclerAdapter(this, medicationsList)
        recyclerView.adapter = adapter

        (findViewById<TextView>(R.id.date_text)).text = Utils.convertTimestampToDate(timestamp)
        (findViewById<TextView>(R.id.time_text)).text = Utils.convertTimestampToTime(timestamp)

        getMedications()
    }

    private fun getMedications(){
        medicationsList.clear()

        Thread{
            val db = ApplicationDb.getInstance(applicationContext)
            for(id in medicationsIds){
                val medication = db!!.medicationDao().loadById(id)
                if(medication != null)
                    medicationsList.add(medication)
            }

            runOnUiThread {
                if(medicationsList.isEmpty())
                    finish()
                else
                    adapter.notifyDataSetChanged()
            }
        }.start()
    }
}