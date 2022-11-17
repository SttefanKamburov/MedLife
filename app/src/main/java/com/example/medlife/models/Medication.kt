package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Medication (
    @PrimaryKey(autoGenerate = true)        var id              : Long = 0,
    @ColumnInfo(name = "name")              var Name            : String = "",
    @ColumnInfo(name = "icon")              var Icon            : String = "",
    @ColumnInfo(name = "dosage")            var Dosage          : String = "",
    @ColumnInfo(name = "taking_frequency")  var TakingFrequency : String = "",
    @ColumnInfo(name = "max_taking_days")   var MaxTakingDays   : Int = 0
) : Parcelable