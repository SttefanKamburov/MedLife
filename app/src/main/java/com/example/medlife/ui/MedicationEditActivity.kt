package com.example.medlife.ui

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.example.medlife.R
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicationEditActivity : AppCompatActivity() {
    private var currentMedication : Medication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_edit);

        currentMedication = intent.getSerializableExtra("medication") as Medication
        if(currentMedication != null)
        {
            var iconView : ImageView          = findViewById(R.id.medication_edit_icon_view)
            var nameView : EditText           = findViewById(R.id.medication_edit_name_view)
            var dosageView : EditText         = findViewById(R.id.medication_edit_dosage_view)
            var frequencyView : EditText      = findViewById(R.id.medication_edit_frequency_view)
            var maxTakingDaysView : EditText  = findViewById(R.id.medication_edit_taking_days_view)
            var cancelButton : Button         = findViewById(R.id.medication_popup_cancel_button)
            var okButton : Button             = findViewById(R.id.medication_popup_ok_button)

            nameView.setText(currentMedication!!.Name)
            dosageView.setText(currentMedication!!.Dosage)
            frequencyView.setText(currentMedication!!.TakingFrequency)
            maxTakingDaysView.setText(currentMedication!!.MaxTakingDays.toString())

            cancelButton.setOnClickListener{
                finish()
            }

            okButton.setOnClickListener{
                this.lifecycleScope.launch(context = Dispatchers.IO){
                    var db = ApplicationDb.getInstance(applicationContext)
                    if(currentMedication!!.id!! > 0) {
                        currentMedication!!.Name = nameView.text.toString()
                        currentMedication!!.Dosage = dosageView.text.toString()
                        currentMedication!!.TakingFrequency = frequencyView.text.toString()
                        currentMedication!!.MaxTakingDays = maxTakingDaysView.text.toString().toInt()
                        db!!.medicationDao().update(currentMedication!!)
                    }else{
                        db!!.medicationDao().insert(Medication(
                            null, nameView.text.toString(), null, dosageView.text.toString(), frequencyView.text.toString(),maxTakingDaysView.text.toString().toInt()))
                    }
                }
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
