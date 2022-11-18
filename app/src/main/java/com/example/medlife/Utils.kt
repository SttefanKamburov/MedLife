package com.example.medlife

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object{
        const val INTENT_TRANSFER_MEDICATION    = "medication"
        const val DATE_FORMAT_SLASHES           = "dd/MM/yyyy"

        fun convertTimestampToDate(timestamp: Long?): String {
            val dateFormat = SimpleDateFormat(DATE_FORMAT_SLASHES, Locale.US)
            return try {
                val calendar = Calendar.getInstance()
                if (timestamp != null) {
                    calendar.timeInMillis = timestamp
                }
                dateFormat.format(calendar.time)
            } catch (e: java.lang.Exception) {
                ""
            }
        }

        fun tintDrawable(drawable: Drawable, tint: Int): Drawable? {
            val drawableWrap = DrawableCompat.wrap(drawable)
            return try {
                val copyDrawable = drawableWrap.constantState!!.newDrawable().mutate()
                DrawableCompat.setTint(copyDrawable, tint)
                DrawableCompat.setTintMode(copyDrawable, PorterDuff.Mode.SRC_ATOP)
                copyDrawable
            } catch (e: Exception) {
                e.printStackTrace()
                drawableWrap
            }
        }
    }
}