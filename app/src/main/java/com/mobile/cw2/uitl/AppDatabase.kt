package com.mobile.cw2.uitl

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QA::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qaDao() : QADao
}