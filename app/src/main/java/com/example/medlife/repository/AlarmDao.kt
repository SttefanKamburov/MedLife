package com.example.medlife.repository

import androidx.room.*
import com.example.medlife.models.Alarm

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)

    @Query("delete from alarm where reminder_id = :id")
    fun deleteAllForReminder(id: Long)

    @Query("select timestamp from alarm where timestamp > :timestamp order by timestamp asc")
    fun getNextAlarmTimestamp(timestamp: Long): Long?

}