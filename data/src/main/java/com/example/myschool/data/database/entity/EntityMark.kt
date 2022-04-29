package com.example.myschool.data.database.entity

import androidx.room.Entity
import com.example.myschool.data.database.entity.EntityMark.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["date", "lessonId", "mark", "number"]
)
data class EntityMark(
    val date: String,

    val lessonId: Int,

    val mark: Int,

    val description: String,

    val number: Int,
) {
    companion object {
        const val TABLE_NAME = "marks"
    }
}
