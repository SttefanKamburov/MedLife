package com.example.medlife

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.medlife.models.Reminder
import com.example.medlife.models.ReminderTime
import com.example.medlife.repository.ApplicationDb
import com.example.medlife.ui.activities.ReminderActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val preferences     = PreferenceManager.getDefaultSharedPreferences(context!!)
        val requestCode     = preferences.getInt(Utils.ALARM_REQUEST_CODE, 100)
        val notificationId  = preferences.getInt(Utils.NOTIFICATION_ID, 1)

        Thread {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND        , 0)
            calendar.set(Calendar.MILLISECOND   , 0)

            val db = ApplicationDb.getInstance(context.applicationContext)
            val remindersListForToday = arrayListOf<Reminder>()
            remindersListForToday.addAll(db!!.reminderDao().loadByDate(calendar.timeInMillis))

            val remindersToShow = arrayListOf<Reminder>()
            for(reminder in remindersListForToday){
                if(db.reminderTimeDao().loadByReminderIdHourAndMinute(
                        reminder.id,
                        calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE))
                        .isNotEmpty()
                ){
                    val medication = db.medicationDao().loadById(reminder.medicationId)
                    if(medication != null){
                        val reminderTime = ReminderTime()
                        reminderTime.hour = calendar.get(Calendar.HOUR)
                        reminderTime.minute = calendar.get(Calendar.MINUTE)

                        reminder.timesList.add(reminderTime)
                        reminder.medicationName = medication.name

                        remindersToShow.add(reminder)
                    }
                }
            }

            //if(remindersToShow.isNotEmpty()) {
                showNotification(context, remindersToShow, calendar.timeInMillis, notificationId)
                preferences.edit().putInt(Utils.NOTIFICATION_ID, notificationId + 1).apply()
            //}

            calendar.add(Calendar.MINUTE, 1)
            val nextAlarm = db.alarmDao().getNextAlarmTimestamp(calendar.timeInMillis)
            if(nextAlarm != null){
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode + 1,
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm, pendingIntent)

                preferences.edit().putInt(Utils.ALARM_REQUEST_CODE, requestCode + 1).apply()
                preferences.edit().putLong(Utils.NEXT_ALARM_TIME_MILLIS, nextAlarm).apply()
            }
        }.start()
    }

    private fun showNotification(context: Context, remindersToShow : ArrayList<Reminder>, timestamp: Long, notificationId : Int){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val medications = arrayListOf<Long>()
        for(reminder in remindersToShow){
            medications.add(reminder.medicationId)
        }

        val intent = Intent(context, ReminderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(Utils.INTENT_TRANSFER_MEDICATIONS, medications)
        intent.putExtra(Utils.INTENT_TRANSFER_TIMESTAMP, timestamp)
        val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var message = context.getString(R.string.you_need_to_take_the_following_medications_colons)
        for(reminder in remindersToShow){
            message += "\n" + reminder.medicationName
        }

        val builder = NotificationCompat.Builder(context, Utils.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setAutoCancel(true)
            .setLights(Color.GREEN, 500, 5000)
            .setSound(Uri.parse(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()))
            .setVibrate(longArrayOf(300, 200, 300, 200, 300))
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setContentIntent(pendingIntent)
        //try {
            notificationManager.notify(notificationId, builder.build())
        //} catch (e: Exception) {
         //   e.printStackTrace()
        //}
    }
}