package com.example.myschool.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.myschool.data.database.entity.EntitySchedule.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["date", "lessonId", "timeRun"]
)
data class EntitySchedule(
    val date: String,

    val lessonId: Int,

    val timeRun: String,

    val timeEnd: String,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val theme: String,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val homework: String
) {
    companion object {
        const val TABLE_NAME = "schedules"
    }
}
