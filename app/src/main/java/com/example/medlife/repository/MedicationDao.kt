package com.example.medlife.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.medlife.models.Medication

@Dao
interface MedicationDao {
    @Query("select * from medication")
    fun getAll(): Array<Medication>

    @Query("select * from medication where id = :id")
    fun loadById(id: Int): Medication

    //@Query("select * from medication where name = :name")
    //fun loadByName(name: String) : String

    @Update
    fun update(medication: Medication)

    @Insert
    fun insert(medication: Medication)

    @Delete
    fun delete(medication: Medication)
}