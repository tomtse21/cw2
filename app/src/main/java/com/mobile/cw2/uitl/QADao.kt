package com.mobile.cw2.uitl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QADao {
    @Query("Select * from qaTable")
    fun loadAllQa() : List<QA>

    @Insert
    fun insertAll(vararg qa : QA)
}
