package com.example.medlife.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medlife.Utils
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class MedicationDate (
    @PrimaryKey(autoGenerate = true)        var id              : Long = 0,
    @ColumnInfo(name = "medication_id")     var medicationId    : Long = 0,
    @ColumnInfo(name = "date")              var takeDate        : Long = 0,
        ): Parcelable