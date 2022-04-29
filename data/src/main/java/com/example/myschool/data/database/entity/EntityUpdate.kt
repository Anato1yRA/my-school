package com.example.myschool.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myschool.data.database.entity.EntityUpdate.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)
class EntityUpdate(
    @PrimaryKey(autoGenerate = false)
    val up: String
) {
    companion object {
        const val TABLE_NAME = "update_data"
    }
}