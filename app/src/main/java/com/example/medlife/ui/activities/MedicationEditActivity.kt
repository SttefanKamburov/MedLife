package com.example.medlife.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.google.android.material.textfield.TextInputLayout
import android.text.TextWatcher

class MedicationEditActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconView           : ImageView
    private lateinit var nameView           : EditText
    private lateinit var dosageView         : EditText
    private lateinit var frequencyView      : EditText
    private lateinit var maxTakingDaysView  : EditText
    private lateinit var addEditBtn         : Button

    private var currentMedication           : Medication? = null
    private var isEdit                      : Boolean     = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_edit)

        if(intent.hasExtra(Utils.INTENT_TRANSFER_MEDICATION)) {
            isEdit = true
            currentMedication = intent.getParcelableExtra(Utils.INTENT_TRANSFER_MEDICATION)
        }
        else
            currentMedication = Medication()

        init()
    }

    private fun init(){
        iconView            = findViewById(R.id.medication_edit_icon_view)
        nameView            = findViewById(R.id.name_edt)
        dosageView          = findViewById(R.id.dosage_edt)
        frequencyView       = findViewById(R.id.frequency_edt)
        maxTakingDaysView   = findViewById(R.id.max_taking_days_edt)
        addEditBtn          = findViewById(R.id.add_edit_btn)

        setWatcher(nameView)
        setWatcher(dosageView)
        setWatcher(frequencyView)
        setWatcher(maxTakingDaysView)

        findViewById<ImageView>(R.id.back_arrow_image_view).setOnClickListener(this)
        addEditBtn.setOnClickListener(this)

        if(isEdit){
            findViewById<TextView>(R.id.toolbar_title_text_view).text = getString(R.string.edit_medication)
            addEditBtn.text = getString(R.string.save)

            val deleteImage = findViewById<ImageView>(R.id.right_image_view)
            deleteImage.setImageResource(R.drawable.delete_icon)
            deleteImage.setOnClickListener(this)

            setData()
        }
        else{
            findViewById<TextView>(R.id.toolbar_title_text_view).text = getString(R.string.add_medication)
            addEditBtn.text = getString(R.string.add)
        }
    }

    private fun setData(){
        nameView.setText(currentMedication!!.Name)
        dosageView.setText(currentMedication!!.Dosage)
        frequencyView.setText(currentMedication!!.TakingFrequency)
        maxTakingDaysView.setText(currentMedication!!.MaxTakingDays.toString())
    }

    private fun setWatcher(editText : EditText){
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                (editText.parent.parent as TextInputLayout).isErrorEnabled = false
            }
        })
    }

    override fun onClick(v: View?) {
        if(v?.id == addEditBtn.id) {
            if (validate()) {
                addEditMedication()
            }
        }
        else if (v?.id == R.id.back_arrow_image_view)
            onBackPressed()
        else if (v?.id == R.id.right_image_view)
            deleteMedication()
    }

    private fun deleteMedication(){
        Thread {
            val db = ApplicationDb.getInstance(applicationContext)
            db!!.medicationDao().delete(currentMedication!!)

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }.start()
    }

    private fun validate() : Boolean {
        var isValid = true

        if(nameView.text.toString().trim().isEmpty()){
            (nameView.parent.parent as TextInputLayout).isErrorEnabled = true
            (nameView.parent.parent as TextInputLayout).error = getString(R.string.please_enter_name)
            isValid = false
        }

        if(dosageView.text.toString().trim().isEmpty()){
            (dosageView.parent.parent as TextInputLayout).isErrorEnabled = true
            (dosageView.parent.parent as TextInputLayout).error = getString(R.string.please_enter_dosage)
            isValid = false
        }

        if(frequencyView.text.toString().trim().isEmpty()){
            (frequencyView.parent.parent as TextInputLayout).isErrorEnabled = true
            (frequencyView.parent.parent as TextInputLayout).error = getString(R.string.please_enter_frequency)
            isValid = false
        }

        if(maxTakingDaysView.text.toString().trim().isEmpty()){
            (maxTakingDaysView.parent.parent as TextInputLayout).isErrorEnabled = true
            (maxTakingDaysView.parent.parent as TextInputLayout).error = getString(R.string.please_enter_max_taking_days)
            isValid = false
        }

        return isValid
    }

    private fun addEditMedication(){
        currentMedication!!.Name = nameView.text.toString()
        currentMedication!!.Dosage = dosageView.text.toString()
        currentMedication!!.TakingFrequency = frequencyView.text.toString()
        currentMedication!!.MaxTakingDays = maxTakingDaysView.text.toString().toInt()

        Thread {
            val db = ApplicationDb.getInstance(applicationContext)
            if (isEdit) {
                db!!.medicationDao().update(currentMedication!!)
            } else {
                db!!.medicationDao().insert(currentMedication!!)
            }

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }.start()
    }
}
