package com.example.myschool.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myschool.data.database.entity.EntityLesson.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [
        Index("name", unique = true)
    ]
)
data class EntityLesson(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val name: String
) {
    companion object {
        const val TABLE_NAME = "lessons"
    }
}
