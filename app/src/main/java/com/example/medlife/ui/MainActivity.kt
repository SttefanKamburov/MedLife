package com.example.medlife.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.adapters.MedicationDtoRecyclerAdapter
import com.example.medlife.ui.fragments.MedicationFragment

class MainActivity : AppCompatActivity() {

    var medicationEditActivity                  : MedicationEditActivity? = null
    private val allMedicationsList              : ArrayList<Medication> = arrayListOf()
    private lateinit var allMedicationsAdapter  : MedicationDtoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity);

        allMedicationsAdapter = MedicationDtoRecyclerAdapter(this.applicationContext, allMedicationsList)
        allMedicationsAdapter.setOnItemClickListener(object  : MedicationDtoRecyclerAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                this?.let{
                    (this as MainActivity).goToAddEditMedication(allMedicationsList[position])
                }
            }

            override fun onDeleteClick(position: Int) {
                TODO("Not yet implemented")
            }
        })

        val recyclerView: RecyclerView = this.findViewById(R.id.all_medications_recyclerview)
        recyclerView.layoutManager      = LinearLayoutManager(this)
        recyclerView.adapter            = allMedicationsAdapter

        getAllMedications()
    }

    fun goToAddEditMedication(medication : Medication?){
        val intent = Intent(this, MedicationEditActivity::class.java)
        if(medication != null)
            intent.putExtra(Utils.INTENT_TRANSFER_MEDICATION, medication)
        addEditMedicationLauncher.launch(intent)
    }

    private val addEditMedicationLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if(medicationEditActivity != null)
                    this!!.getAllMedications()
            }
        }

    fun getAllMedications(){
        Thread {
            allMedicationsList.clear()
            if(this != null){
                ApplicationDb.getInstance(this.applicationContext)!!.medicationDao().insert(
                    Medication(1, "test", "test", "test", "test")
                )
                allMedicationsList.addAll(ApplicationDb.getInstance(this.applicationContext as Context)!!.medicationDao().getAll())
                this?.runOnUiThread {
                    //addMedicationBtn.visibility = View.VISIBLE
                    allMedicationsAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }
}