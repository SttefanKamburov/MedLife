package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.BLOB
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Medication (
    @PrimaryKey(autoGenerate = true)                var id              : Long = 0,
    @ColumnInfo(name = "name")                      var name            : String = "",
    @ColumnInfo(name = "icon", typeAffinity = BLOB) var icon            : ByteArray? = null,
    @ColumnInfo(name = "dosage")                    var dosage          : String = "",
    @ColumnInfo(name = "taking_frequency")          var takingFrequency : String = "",
    @ColumnInfo(name = "max_taking_days")           var maxTakingDays   : Int = 0
) : Parcelable