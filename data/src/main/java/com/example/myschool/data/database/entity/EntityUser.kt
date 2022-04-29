package com.example.myschool.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myschool.data.database.entity.EntityUser.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [
        Index("login", unique = true)
    ]
)
class EntityUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val login: String,

    val password: String
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}