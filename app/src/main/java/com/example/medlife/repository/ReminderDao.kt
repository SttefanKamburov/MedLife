package com.example.medlife.repository

import androidx.room.*
import com.example.medlife.models.Reminder

@Dao
interface ReminderDao {
    @Query("select * from reminder")
    fun getAll(): Array<Reminder>

    @Query("select * from reminder where id = :id")
    fun loadById(id: Long): Reminder

    @Query("select * from reminder where medication_id = :medicationId")
    fun loadByMedication(medicationId: Long): Array<Reminder>

    @Query("select * from reminder where date_from < :timestamp and date_to > :timestamp")
    fun loadByDate(timestamp: Long): Array<Reminder>

    @Update
    fun update(reminder: Reminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder) : Long

    @Delete
    fun delete(reminder: Reminder)

    @Query("delete from reminder where medication_id = :medicationId")
    fun deleteForMedication(medicationId: Long)
}