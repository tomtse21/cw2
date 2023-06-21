package com.mobile.cw2.uitl

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QADao {
    @Query("Select * from qaTable")
    fun loadAllQa() : List<QA>

    @Insert
    fun insertAll(vararg qa : QA)

    @Delete
    fun delete(vararg ag: QA?)

    @Update
    suspend fun updateQa(vararg qa: QA?)


}
