package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.medlife.Utils
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Parcelize
@Entity
class MedicationDate (
    @PrimaryKey(autoGenerate = true)        var id              : Long = 0,
    @ColumnInfo(name = "medication_id")     var medicationId    : Long = 0,
    @ColumnInfo(name = "date")              var takeDate        : Date = Utils.toDate(System.currentTimeMillis()),
        ): Parcelable