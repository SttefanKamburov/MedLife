package com.example.medlife.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Medication
import com.example.medlife.repository.ApplicationDb
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class AddEditMedicationActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val REQUEST_CODE_READ_STORAGE_PERMISSION  = 1
        private const val REQUEST_CODE_WRITE_STORAGE_PERMISSION = 2
        private const val REQUEST_CODE_CAMERA_PERMISSION        = 3
    }

    private lateinit var iconImageView          : ImageView
    private lateinit var nameEditText           : EditText
    private lateinit var dosageEditText         : EditText
    private lateinit var frequencyEditText      : EditText
    private lateinit var maxTakingDaysEditText  : EditText
    private lateinit var addEditBtn             : Button

    private var currentMedication               : Medication?   = null
    private var isEdit                          : Boolean       = false
    private var currentPhotoPath                                = ""
    private var createdImageFile                : File?         = null
    private var bitmap                          : Bitmap?       = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_edit)

        if(intent.hasExtra(Utils.INTENT_TRANSFER_MEDICATION_ID)) {
            isEdit = true
            val id = intent.getLongExtra(Utils.INTENT_TRANSFER_MEDICATION_ID, 0)

            Thread {
                currentMedication = ApplicationDb.getInstance(MedicationEditActivity@this)!!.medicationDao().loadById(id)
                runOnUiThread { init() }
            }.start()
        }
        else {
            currentMedication = Medication()
            init()
        }
    }

    private fun init(){
        iconImageView           = findViewById(R.id.medication_icon)
        nameEditText            = findViewById(R.id.name_edt)
        dosageEditText          = findViewById(R.id.dosage_edt)
        frequencyEditText       = findViewById(R.id.frequency_edt)
        maxTakingDaysEditText   = findViewById(R.id.max_taking_days_edt)
        addEditBtn              = findViewById(R.id.add_edit_btn)

        setWatcher(nameEditText)
        setWatcher(dosageEditText)
        setWatcher(frequencyEditText)
        setWatcher(maxTakingDaysEditText)

        findViewById<ImageView>(R.id.back_arrow_image_view).setOnClickListener(this)
        addEditBtn.setOnClickListener(this)
        iconImageView.setOnClickListener(this)

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
        try{
            if(currentMedication!!.icon != null)
                Utils.setImage(this, iconImageView, currentMedication!!.icon, RoundedCorners(20))
        }catch (e : Exception){
            e.printStackTrace()
        }

        nameEditText.setText(currentMedication!!.name)
        dosageEditText.setText(currentMedication!!.dosage)
        frequencyEditText.setText(currentMedication!!.takingFrequency)
        maxTakingDaysEditText.setText(currentMedication!!.maxTakingDays.toString())
    }

    private fun setBitmap(){
        Glide.with(this)
            .load(if (bitmap == null) R.drawable.default_medication_icon else bitmap)
            .error(R.drawable.default_medication_icon)
            .transform(CenterCrop(), RoundedCorners(20))
            .into(iconImageView)
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
        if (v?.id == R.id.back_arrow_image_view)
            onBackPressed()
        else if (v?.id == iconImageView.id)
            showChooseMethodForSelectingImage()
        else if(v?.id == addEditBtn.id) {
            if (validate()) {
                addEditMedication()
            }
        }
        else if (v?.id == R.id.right_image_view)
            confirmDeleteMedication()
    }

    private fun showChooseMethodForSelectingImage() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE_STORAGE_PERMISSION
                )
                return
            }
            val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_choose_image, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogLayout)
            val alertDialog = builder.show()
            alertDialog.findViewById<View>(R.id.take_picture_btn)!!.setOnClickListener { v: View? ->
                alertDialog.dismiss()
                captureImage()
            }
            alertDialog.findViewById<View>(R.id.select_from_gallery_btn)!!
                .setOnClickListener { v: View? ->
                    alertDialog.dismiss()
                    choosePhotoFromGallery("")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_CAMERA_PERMISSION
            )
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    createImageFile()
                )
                takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                openCameraLauncher.launch(takePictureIntent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun choosePhotoFromGallery(chooserTitle: String?) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_STORAGE_PERMISSION
            )
            return
        }

        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_PICK
        openGalleryLauncher.launch(Intent.createChooser(galleryIntent, chooserTitle))
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "temp"
        val storageType = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var storageDir = File(storageType.toString() + "/MedLife/")

        if (!storageDir.mkdirs()) {
            storageDir = File(externalMediaDirs[0].toString() + "/MedLife/")
            storageDir.mkdirs()
        }

        createdImageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = "file:" + createdImageFile?.absolutePath
        return createdImageFile as File
    }

    private fun validate() : Boolean {
        var isValid = true

        if(nameEditText.text.toString().trim().isEmpty()){
            (nameEditText.parent.parent as TextInputLayout).isErrorEnabled = true
            (nameEditText.parent.parent as TextInputLayout).error = getString(R.string.please_enter_name)
            isValid = false
        }

        if(dosageEditText.text.toString().trim().isEmpty()){
            (dosageEditText.parent.parent as TextInputLayout).isErrorEnabled = true
            (dosageEditText.parent.parent as TextInputLayout).error = getString(R.string.please_enter_dosage)
            isValid = false
        }

        if(frequencyEditText.text.toString().trim().isEmpty()){
            (frequencyEditText.parent.parent as TextInputLayout).isErrorEnabled = true
            (frequencyEditText.parent.parent as TextInputLayout).error = getString(R.string.please_enter_frequency)
            isValid = false
        }

        if(maxTakingDaysEditText.text.toString().trim().isEmpty()){
            (maxTakingDaysEditText.parent.parent as TextInputLayout).isErrorEnabled = true
            (maxTakingDaysEditText.parent.parent as TextInputLayout).error = getString(R.string.please_enter_max_taking_days)
            isValid = false
        }

        return isValid
    }

    private fun addEditMedication(){
        if(bitmap != null){
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            bitmap!!.recycle()
            currentMedication!!.icon = byteArray
        }

        currentMedication!!.name            = nameEditText.text.toString()
        currentMedication!!.dosage          = dosageEditText.text.toString()
        currentMedication!!.takingFrequency = frequencyEditText.text.toString()
        currentMedication!!.maxTakingDays   = maxTakingDaysEditText.text.toString().toInt()

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

    private fun confirmDeleteMedication(){
        val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_are_you_sure, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogLayout)
        val alertDialog = builder.show()
        alertDialog.findViewById<TextView>(R.id.info_text)?.text = getString(R.string.are_you_sure_you_want_to_delete_this_medication)
        alertDialog.findViewById<View>(R.id.cancel_btn)!!.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.findViewById<View>(R.id.confirm_btn)!!.setOnClickListener {
            alertDialog.dismiss()
            deleteMedication()
        }
    }

    private fun deleteMedication(){
        Thread {
            val db = ApplicationDb.getInstance(applicationContext)
            db!!.medicationDao().delete(currentMedication!!)
            db.reminderDao().deleteForMedication(currentMedication!!.id)

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            showChooseMethodForSelectingImage()
        else if (requestCode == REQUEST_CODE_READ_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            choosePhotoFromGallery("")
        else if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            captureImage()
    }

    private val openCameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try{
                    bitmap = Utils.convertSelectedImage(this, Uri.fromFile(createdImageFile), true, createdImageFile!!.absolutePath)
                    setBitmap()
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                bitmap = Utils.convertSelectedImage(this, uri, false, "")
                setBitmap()
            }
        }
}
