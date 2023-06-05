package com.mobile.cw2.uitl

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "qaTable")
data class QA (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = BaseColumns._ID) val id : Int,
    @ColumnInfo(name = "question") val question : String?,
    @ColumnInfo(name = "answer") val answer : String?
)