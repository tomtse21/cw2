package com.mobile.cw2.uitl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QA::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qaDao() : QADao

    companion object{

        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{

            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance =Room.databaseBuilder(context, AppDatabase::class.java, "qaDB")
                    .allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }

        }
    }
}