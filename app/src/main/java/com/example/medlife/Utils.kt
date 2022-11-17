package com.example.medlife

import java.util.*

class Utils {

    companion object{
        val INTENT_TRANSFER_MEDICATION : String = "medication"

        fun toDate(dateLong: Long): Date {
            return dateLong.let { Date(it) }
        }

        fun fromDate(date: Date): Long {
            return date.getTime()
        }
    }
}