package com.example.medlife.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb

class MedicationEditActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconView           : ImageView
    private lateinit var nameView           : EditText
    private lateinit var dosageView         : EditText
    private lateinit var frequencyView      : EditText
    private lateinit var maxTakingDaysView  : EditText
    private lateinit var cancelButton       : Button
    private lateinit var okButton           : Button

    private var currentMedication           : Medication? = null
    private var isEdit                      : Boolean     = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_edit);

        init()

        if(intent.hasExtra(Utils.INTENT_TRANSFER_MEDICATION)) {
            isEdit = true
            currentMedication = intent.getParcelableExtra(Utils.INTENT_TRANSFER_MEDICATION)
            setData()
        }
        else
            currentMedication = Medication()
    }

    private fun init(){
        iconView            = findViewById(R.id.medication_edit_icon_view)
        nameView            = findViewById(R.id.medication_edit_name_view)
        dosageView          = findViewById(R.id.medication_edit_dosage_view)
        frequencyView       = findViewById(R.id.medication_edit_frequency_view)
        maxTakingDaysView   = findViewById(R.id.medication_edit_taking_days_view)
        cancelButton        = findViewById(R.id.medication_popup_cancel_button)
        okButton            = findViewById(R.id.medication_popup_ok_button)

        cancelButton.setOnClickListener(this)
        okButton.setOnClickListener(this)
    }

    private fun setData(){
        nameView.setText(currentMedication!!.Name)
        dosageView.setText(currentMedication!!.Dosage)
        frequencyView.setText(currentMedication!!.TakingFrequency)
        maxTakingDaysView.setText(currentMedication!!.MaxTakingDays.toString())
    }

    override fun onClick(v: View?) {
        if(v?.id == okButton.id)
            addEditMedication()
        else if (v?.id == cancelButton.id)
            onBackPressed()
    }

    private fun addEditMedication(){
        Thread {
            val db = ApplicationDb.getInstance(applicationContext)
            if (isEdit) {
                currentMedication!!.Name = nameView.text.toString()
                currentMedication!!.Dosage = dosageView.text.toString()
                currentMedication!!.TakingFrequency = frequencyView.text.toString()
                currentMedication!!.MaxTakingDays = maxTakingDaysView.text.toString().toInt()
                db!!.medicationDao().update(currentMedication!!)
            } else {
                db!!.medicationDao().insert(
                    Medication(
                        0,
                        nameView.text.toString(),
                        "",
                        dosageView.text.toString(),
                        frequencyView.text.toString(),
                        maxTakingDaysView.text.toString().toInt()
                    )
                )
            }

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }.start()
    }
}
