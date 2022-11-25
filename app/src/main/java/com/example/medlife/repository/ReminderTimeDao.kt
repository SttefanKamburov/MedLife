package com.example.medlife.repository

import androidx.room.*
import com.example.medlife.models.ReminderTime

@Dao
interface ReminderTimeDao {
    @Query("select * from remindertime")
    fun getAll(): Array<ReminderTime>

    @Query("select * from remindertime where id = :id")
    fun loadById(id: Long): ReminderTime

    @Query("select * from remindertime where reminder_id = :id")
    fun loadByReminderId(id: Long): Array<ReminderTime>

    @Update
    fun update(reminderTime: ReminderTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminderTime: ReminderTime)

    @Delete
    fun delete(reminderTime: ReminderTime)

    @Query("delete from remindertime where reminder_id = :id")
    fun deleteAllForReminder(id: Long)
}