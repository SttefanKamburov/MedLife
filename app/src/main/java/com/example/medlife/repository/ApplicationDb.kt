package com.example.medlife.repository

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.medlife.models.Medication
import java.security.AccessControlContext

@Database(entities = [Medication::class], version = 1)
abstract class ApplicationDb : RoomDatabase() {
    companion object{
        private var sIntance : ApplicationDb? = null
        const val dbName : String = "medLife"

        fun getInstance(context : Context) : ApplicationDb?{
            if(sIntance == null) {
                synchronized(ApplicationDb::class.java){
                    if(sIntance == null){
                        sIntance = buildDatabase(context);
                        sIntance!!.openHelper.readableDatabase;
                    }
                }
            }

            return sIntance;
        }

        private fun buildDatabase(appContext : Context) : ApplicationDb{
            return Room.databaseBuilder(
                appContext,
                ApplicationDb::class.java,
                dbName)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback(){
                    override fun onOpen(@NonNull db: SupportSQLiteDatabase){
                        super.onOpen(db);
                    }
                }).build()
        }
    }

    public abstract fun medicationDao() : MedicationDao
}