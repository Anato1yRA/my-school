package com.example.myschool.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myschool.data.database.entity.EntityReport.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
data class EntityReport(
    val text: String,

    @PrimaryKey
    val date: Long? = System.currentTimeMillis()
) {
    companion object {
        const val TABLE_NAME = "report"
    }
}
