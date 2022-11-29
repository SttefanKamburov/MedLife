package com.example.medlife

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class Utils {

    companion object{
        const val INTENT_TRANSFER_MEDICATION_ID = "medication_id"
        const val INTENT_TRANSFER_REMINDER_ID   = "reminder_id";
        const val INTENT_TRANSFER_MEDICATIONS   = "medications"
        const val INTENT_TRANSFER_TIMESTAMP     = "timestamp"

        const val DATE_FORMAT_SLASHES           = "dd/MM/yyyy"
        const val DATE_FORMAT_TIME              = "HH:mm"

        const val NOTIFICATION_CHANNEL_ID       = "med_life_notification_channel_id"
        const val NOTIFICATION_CHANNEL_NAME     = "Med Life Notifications"
        const val NEXT_ALARM_TIME_MILLIS        = "next_alarm_time_millis"
        const val ALARM_REQUEST_CODE            = "alarm_request_code"
        const val NOTIFICATION_ID               = "notification_id"

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

        fun convertTimestampToTime(timestamp: Long?): String {
            val dateFormat = SimpleDateFormat(DATE_FORMAT_TIME, Locale.US)
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

        fun getDisplayTime(hour : Int, minute : Int) : String {
            return "$hour:${if(minute > 9) minute.toString() else "0${minute}"}"
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

        fun setImage(context : Context, imageView : ImageView, image : ByteArray?, transformation : Transformation<Bitmap>){
            Glide.with(context)
                .load(image)
                .error(R.drawable.default_medication_icon)
                .transform(CenterCrop(), transformation)
                .into(imageView)
        }

        fun convertSelectedImage(context: Context, selectedImage: Uri?, fromCamera : Boolean, path : String): Bitmap? {
            var imageStream: InputStream? = null
            try {
                imageStream = context.contentResolver.openInputStream(selectedImage!!)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(imageStream, null, bmOptions)
            try {
                imageStream = context.contentResolver.openInputStream(selectedImage!!)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val target = 500
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = min(bmOptions.outWidth / target, bmOptions.outHeight / target)

            var yourSelectedImage = BitmapFactory.decodeStream(imageStream, null, bmOptions)
            try {
                var absolutePath = ""
                if(fromCamera){
                    absolutePath = path
                }
                else{
                    absolutePath = getRealPathFromURI(context, selectedImage)
                }

                val ei = ExifInterface(absolutePath)
                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> yourSelectedImage =
                        rotateBitmap(yourSelectedImage, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> yourSelectedImage =
                        rotateBitmap(yourSelectedImage, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> yourSelectedImage =
                        rotateBitmap(yourSelectedImage, 270f)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return yourSelectedImage
        }

        private fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(columnIndex)
            } finally {
                cursor?.close()
            }
        }

        private fun rotateBitmap(src: Bitmap?, degree: Float): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(degree)
            return src?.let { Bitmap.createBitmap(it, 0, 0, src.width, src.height, matrix, true) }
        }
    }
}