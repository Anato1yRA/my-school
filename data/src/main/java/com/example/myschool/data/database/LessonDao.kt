package com.example.myschool.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myschool.data.database.entity.*

@Dao
interface LessonDao {
    @Query("SELECT id FROM ${EntityUser.TABLE_NAME} WHERE login LIKE :login")
    suspend fun getUserIdByLogin(login: String): Int?

    @Query("SELECT name FROM ${EntityLesson.TABLE_NAME} WHERE LOWER(name) in (:nameList)")
    suspend fun getListLessonByName(nameList: List<String>): List<String>

    @Query("SELECT id, name FROM ${EntityLesson.TABLE_NAME} ORDER BY name ASC")
    suspend fun getListLesson(): List<EntityLesson>

    @Query("SELECT name FROM ${EntityLesson.TABLE_NAME} WHERE id LIKE :id")
    suspend fun getLessonNameById(id: Int): String

    @Query("SELECT date, lessonId, mark, description, 0 AS number FROM ${EntityMark.TABLE_NAME} ORDER BY lessonId ASC, date ASC, mark ASC, description ASC")
    suspend fun getListMark(): MutableList<EntityMark>

    @Query("SELECT * FROM ${EntityMark.TABLE_NAME} WHERE lessonId LIKE :lessonId ORDER BY date ASC, mark ASC, description ASC")
    suspend fun getListMarkById(lessonId: Int): List<EntityMark>

    @Query("SELECT up FROM ${EntityUpdate.TABLE_NAME}")
    fun checkUpdateData(): LiveData<String>

    @Query("SELECT * FROM ${EntityReport.TABLE_NAME} WHERE date != (SELECT MAX(date) FROM ${EntityReport.TABLE_NAME}) ORDER BY date DESC")
    suspend fun getReport(): List<EntityReport>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: EntityUser)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessonList(lessonList: List<EntityLesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleList(scheduleList: List<EntitySchedule>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(markList: List<EntityMark>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: EntityReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(up: EntityUpdate)


    @Transaction
    suspend fun clearData() {
        deleteAllUsers()
        deleteAllLessons()
        deleteAllMarks()
        deleteAllSchedules()
        clearIndex("users")
        clearIndex("lessons")
        clearIndex("marks")
        clearIndex("schedules")
    }

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM lessons")
    suspend fun deleteAllLessons()

    @Query("DELETE FROM marks")
    suspend fun deleteAllMarks()

    @Query("DELETE FROM schedules")
    suspend fun deleteAllSchedules()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name LIKE :tableName")
    suspend fun clearIndex(tableName: String)
}