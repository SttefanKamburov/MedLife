package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Reminder (
    @PrimaryKey(autoGenerate = true)    var id              : Long          = 0,
    @ColumnInfo(name = "medication_id") var medicationId    : Long          = 0,
    @ColumnInfo(name = "date_from")     var dateFrom        : Long          = 0,
    @ColumnInfo(name = "date_to")       var dateTo          : Long          = 0,
    @Ignore var medicationName  : String                    = "",
    @Ignore var medicationImage : ByteArray?                = null,
    @Ignore var timesList       : ArrayList<ReminderTime>   = arrayListOf()
) : Parcelable