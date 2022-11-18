package com.example.medlife.repository

import androidx.room.*
import com.example.medlife.models.Medication

@Dao
interface MedicationDao {
    @Query("select * from medication")
    fun getAll(): Array<Medication>

    @Query("select * from medication where id = :id")
    fun loadById(id: Long): Medication

    //@Query("select * from medication where name = :name")
    //fun loadByName(name: String) : String

    @Update
    fun update(medication: Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medication: Medication)

    @Delete
    fun delete(medication: Medication)
}