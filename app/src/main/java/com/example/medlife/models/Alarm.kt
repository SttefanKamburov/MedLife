package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Alarm (
    @PrimaryKey(autoGenerate = true)    var id              : Long  = 0,
    @ColumnInfo(name = "reminder_id")   var reminderId      : Long  = 0,
    @ColumnInfo(name = "timestamp")     var timestamp       : Long  = 0
) : Parcelable
