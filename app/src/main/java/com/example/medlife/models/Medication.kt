package com.example.medlife.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Medication (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") var Name : String? = null,
    @ColumnInfo(name = "icon") var Icon : String? = null,
    @ColumnInfo(name = "dosage") var Dosage : String? = null,
    @ColumnInfo(name = "taking_frequency") var TakingFrequency : String? = null,
    @ColumnInfo(name = "max_taking_days") var MaxTakingDays : Int? = null
) : java.io.Serializable