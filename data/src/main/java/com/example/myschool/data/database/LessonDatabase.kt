package com.example.myschool.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myschool.data.database.entity.*

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        EntityUser::class,
        EntityLesson::class,
        EntitySchedule::class,
        EntityMark::class,
        EntityReport::class,
        EntityUpdate::class
    ]
)
abstract class LessonDatabase : RoomDatabase() {

    abstract fun lessonDao(): LessonDao
}