package com.example.myschool.data

import android.app.Application
import androidx.room.Room
import com.example.myschool.data.database.LessonDao
import com.example.myschool.data.database.LessonDatabase
import org.koin.dsl.module

fun providesDatabase(application: Application): LessonDatabase =
    Room.databaseBuilder(application, LessonDatabase::class.java, "database.db")
        .build()

fun providesDao(db: LessonDatabase): LessonDao = db.lessonDao()

val roomModule = module {

    single { providesDatabase(get()) }

    single { providesDao(get()) }
}
