package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ReminderTime (
    @PrimaryKey(autoGenerate = true)    var id              : Long  = 0,
    @ColumnInfo(name = "reminder_id")   var reminderId      : Long  = 0,
    @ColumnInfo(name = "hour")          var hour            : Int   = 0,
    @ColumnInfo(name = "minute")        var minute          : Int   = 0
) : Parcelable