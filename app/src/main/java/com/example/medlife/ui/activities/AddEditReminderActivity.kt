package com.example.medlife.ui.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.medlife.AlarmReceiver
import com.example.medlife.R
import com.example.medlife.Utils
import com.example.medlife.models.Alarm
import com.example.medlife.models.Medication
import com.example.medlife.models.Reminder
import com.example.medlife.models.ReminderTime
import com.example.medlife.repository.ApplicationDb
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class AddEditReminderActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var iconImageView      : ImageView
    private lateinit var nameText           : TextView
    private lateinit var dateFromEdt        : EditText
    private lateinit var dateToEdt          : EditText
    private lateinit var timesContainer     : LinearLayout
    private lateinit var addTimeBtn         : TextView
    private lateinit var addEditBtn         : Button

    private var isEdit                      : Boolean                   = false
    private var reminder                    : Reminder?                 = null
    private var medication                  : Medication?               = null
    private var times                       : ArrayList<ReminderTime>   = arrayListOf()
    private var selectedTimeIndex           : Int                       = 0
    private var fromDateMillis              : Long                      = 0
    private var toDateMillis                : Long                      = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reminder)

        if(intent.hasExtra(Utils.INTENT_TRANSFER_REMINDER_ID)) {
            isEdit = true
            val id = intent.getLongExtra(Utils.INTENT_TRANSFER_REMINDER_ID, 0)

            Thread {
                val db          = ApplicationDb.getInstance(MedicationEditActivity@this)
                reminder        = db!!.reminderDao().loadById(id)
                fromDateMillis  = reminder!!.dateFrom
                toDateMillis    = reminder!!.dateTo
                medication      = db.medicationDao().loadById(reminder!!.medicationId)

                times.addAll(db.reminderTimeDao().loadByReminderId(id))

                runOnUiThread { init() }
            }.start()
        }
        else {
            val medicationId = intent.getLongExtra(Utils.INTENT_TRANSFER_MEDICATION_ID, 0)
            Thread {
                reminder    = Reminder()
                val db      = ApplicationDb.getInstance(MedicationEditActivity@this)
                medication  = db!!.medicationDao().loadById(medicationId)

                runOnUiThread { init() }
            }.start()
        }
    }

    private fun init(){
        iconImageView   = findViewById(R.id.medication_icon)
        nameText        = findViewById(R.id.medication_name)
        dateFromEdt     = findViewById(R.id.date_from_edt)
        dateToEdt       = findViewById(R.id.date_to_edt)
        timesContainer  = findViewById(R.id.times_container)
        addTimeBtn      = findViewById(R.id.add_time_btn)
        addEditBtn      = findViewById(R.id.add_edit_btn)

        findViewById<ImageView>(R.id.back_arrow_image_view).setOnClickListener(this)
        dateFromEdt.setOnClickListener(this)
        dateToEdt.setOnClickListener(this)
        addEditBtn.setOnClickListener(this)
        addTimeBtn.setOnClickListener(this)

        setWatcher(dateFromEdt)
        setWatcher(dateToEdt)

        setMedicationData()

        if(isEdit){
            findViewById<TextView>(R.id.toolbar_title_text_view).text = getString(R.string.edit_reminder)
            addEditBtn.text = getString(R.string.save)

            val deleteImage = findViewById<ImageView>(R.id.right_image_view)
            deleteImage.setImageResource(R.drawable.delete_icon)
            deleteImage.setOnClickListener(this)

            setDates()
            refreshTimes()
        }
        else{
            findViewById<TextView>(R.id.toolbar_title_text_view).text = getString(R.string.add_reminder)
            addEditBtn.text = getString(R.string.add)
        }
    }

    private fun setMedicationData(){
        try{
            if(medication!!.icon != null)
                Utils.setImage(this, iconImageView, medication!!.icon, RoundedCorners(20))
        }catch (e : Exception){
            e.printStackTrace()
        }

        nameText.text = medication!!.name
    }

    private fun setDates(){
        if(fromDateMillis > 0)
            dateFromEdt.setText(Utils.convertTimestampToDate(fromDateMillis))
        if(toDateMillis > 0)
            dateToEdt.setText(Utils.convertTimestampToDate(toDateMillis))
    }

    private fun refreshTimes(){
        timesContainer.removeAllViews()
        times.sortWith(compareBy ({ it.hour }, {it.minute}))

        times.forEachIndexed { index, reminderTime ->
            addTimeRow(index, reminderTime)
        }
    }

    private fun addTimeRow(indexInList : Int, reminderTime: ReminderTime){
        val child: View = layoutInflater.inflate(R.layout.time_row, null)

        val timeText  = child.findViewById<TextView>(R.id.time_text)
        val deleteBtn = child.findViewById<ImageView>(R.id.delete_icon)

        timeText.text = Utils.getDisplayTime(reminderTime.hour, reminderTime.minute)
        deleteBtn.setOnClickListener {
            selectedTimeIndex = indexInList
            confirmDelete(true)
        }

        timesContainer.addView(child)
    }

    private fun setWatcher(editText : EditText){
        editText.addTextChangedListener(object : TextWatcher {
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
        else if (v?.id == dateFromEdt.id){
            showPickDateDialog(dateFromEdt)
        }
        else if (v?.id == dateToEdt.id){
            showPickDateDialog(dateToEdt)
        }
        else if (v?.id == addTimeBtn.id){
            showPickTimeDialog()
        }
        else if(v?.id == addEditBtn.id) {
            if (validate()) {
                addEditReminder()
            }
        }
        else if (v?.id == R.id.right_image_view)
            confirmDelete(false)
    }

    private fun validate() : Boolean {
        var isValid = true

        if(dateFromEdt.text.toString().trim().isEmpty()){
            (dateFromEdt.parent.parent as TextInputLayout).isErrorEnabled = true
            (dateFromEdt.parent.parent as TextInputLayout).error = getString(R.string.please_choose_date)
            isValid = false
        }

        if(dateToEdt.text.toString().trim().isEmpty()){
            (dateToEdt.parent.parent as TextInputLayout).isErrorEnabled = true
            (dateToEdt.parent.parent as TextInputLayout).error = getString(R.string.please_choose_date)
            isValid = false
        }

        if(times.isEmpty()){
            isValid = false
            Toast.makeText(this@AddEditReminderActivity, getString(R.string.please_add_at_least_one_reminder_time), Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun confirmDelete(isTime : Boolean){
        val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_are_you_sure, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogLayout)
        val alertDialog = builder.show()
        alertDialog.findViewById<TextView>(R.id.info_text)?.text =
            getString(
                if(isTime) R.string.are_you_sure_you_want_to_delete_this_reminder_time
                else R.string.are_you_sure_you_want_to_delete_this_reminder)

        alertDialog.findViewById<View>(R.id.cancel_btn)!!.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.findViewById<View>(R.id.confirm_btn)!!.setOnClickListener {
            alertDialog.dismiss()
            if(isTime) {
                times.removeAt(selectedTimeIndex)
                refreshTimes()
            }
            else
                deleteReminder()
        }
    }

    private fun showPickDateDialog(dateEdt: EditText) {
        val calendar = Calendar.getInstance()

        val dateListener =
            OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(year, monthOfYear, dayOfMonth)

                if (dateEdt === dateFromEdt) {
                    calendar.set(Calendar.HOUR, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    fromDateMillis = calendar.timeInMillis
                }
                else {
                    calendar.set(Calendar.HOUR, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    toDateMillis = calendar.timeInMillis
                }

                setDates()
            }

        if(dateEdt === dateFromEdt && fromDateMillis > 0)
            calendar.timeInMillis = fromDateMillis

        if(dateEdt === dateToEdt && toDateMillis > 0)
            calendar.timeInMillis = toDateMillis

        val datePickerDialog = DatePickerDialog(
            this,
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        if(dateEdt === dateFromEdt && toDateMillis > 0)
            datePickerDialog.datePicker.maxDate = toDateMillis
        if(dateEdt === dateToEdt && fromDateMillis > 0)
            datePickerDialog.datePicker.minDate = fromDateMillis

        datePickerDialog.show()
    }

    private fun showPickTimeDialog(){
        val calendar = Calendar.getInstance()

        val timeListener =
            TimePickerDialog.OnTimeSetListener { _ : TimePicker, selectedHour : Int, selectedMinute : Int ->
                val reminderTime    = ReminderTime()
                reminderTime.hour   = selectedHour
                reminderTime.minute = selectedMinute
                times.add(reminderTime)
                refreshTimes()
            }

        val hour        = calendar.get(Calendar.HOUR_OF_DAY)
        val minute      = calendar.get(Calendar.MINUTE)
        val mTimePicker = TimePickerDialog(this, timeListener, hour, minute, true)

        mTimePicker.setTitle(getString(R.string.select_time))
        mTimePicker.show()
    }

    private fun addEditReminder(){
        reminder!!.medicationId = medication!!.id
        reminder!!.dateFrom     = fromDateMillis
        reminder!!.dateTo       = toDateMillis

        Thread {
            val db          = ApplicationDb.getInstance(applicationContext)
            var reminderId  = if(isEdit) reminder!!.id else 0L

            if (isEdit) {
                db!!.reminderTimeDao().deleteAllForReminder(reminder!!.id)
                db.alarmDao().deleteAllForReminder(reminder!!.id)
                db.reminderDao().update(reminder!!)
            } else {
                reminderId = db!!.reminderDao().insert(reminder!!)
            }

            val todayCalendar = Calendar.getInstance()
            var firstTimestamp = 0L

            for (time in times) {
                time.reminderId = reminderId
                db.reminderTimeDao().insert(time)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = fromDateMillis
                calendar.set(Calendar.HOUR, time.hour)
                calendar.set(Calendar.MINUTE, time.minute)
                calendar.set(Calendar.SECOND, 0)

                while (calendar.timeInMillis < toDateMillis){
                    if(calendar.timeInMillis > todayCalendar.timeInMillis){
                        val alarm = Alarm()
                        alarm.reminderId = reminderId
                        alarm.timestamp = calendar.timeInMillis
                        db.alarmDao().insert(alarm)

                        if(firstTimestamp == 0L)
                            firstTimestamp = calendar.timeInMillis
                    }

                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            if(firstTimestamp > 0){
                val preferences = PreferenceManager.getDefaultSharedPreferences(this@AddEditReminderActivity)
                val nextAlarm   = preferences.getLong(Utils.NEXT_ALARM_TIME_MILLIS, 0L)
                val requestCode = preferences.getInt(Utils.ALARM_REQUEST_CODE, 100)

                if(nextAlarm == 0L || firstTimestamp < nextAlarm){

                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    if(firstTimestamp < nextAlarm){
                        val cancelIntent = PendingIntent.getBroadcast(
                            this@AddEditReminderActivity,
                            requestCode,
                            Intent(this@AddEditReminderActivity, AlarmReceiver::class.java),
                            PendingIntent.FLAG_IMMUTABLE)
                        alarmManager.cancel(cancelIntent)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        this@AddEditReminderActivity,
                        requestCode + 1,
                        Intent(this@AddEditReminderActivity, AlarmReceiver::class.java),
                        PendingIntent.FLAG_IMMUTABLE)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstTimestamp, pendingIntent)

                    preferences.edit().putInt(Utils.ALARM_REQUEST_CODE, requestCode + 1).apply()
                    preferences.edit().putLong(Utils.NEXT_ALARM_TIME_MILLIS, firstTimestamp).apply()
                }
            }

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }

        }.start()
    }

    private fun deleteReminder(){
        Thread {
            val db = ApplicationDb.getInstance(applicationContext)
            db!!.reminderDao().delete(reminder!!)

            runOnUiThread {
                setResult(RESULT_OK)
                finish()
            }
        }.start()
    }


}