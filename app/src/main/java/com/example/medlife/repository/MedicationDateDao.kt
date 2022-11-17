package com.example.medlife.repository

import androidx.room.*
import com.example.medlife.models.MedicationDate

@Dao
interface MedicationDateDao {
    @Query("select * from medication")
    fun getAll(): Array<MedicationDate>

    @Query("select * from medication where id = :id")
    fun loadById(id: Int): MedicationDate

    //@Query("select * from medication where name = :name")
    //fun loadByName(name: String) : String

    @Update
    fun update(medication: MedicationDate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medication: MedicationDate)

    @Delete
    fun delete(medication: MedicationDate)
}