package com.example.myschool.data.domain.repository

import androidx.lifecycle.LiveData

interface DomainRepository {

    suspend fun setNewSession(
        login: String,
        password: String,
        gu: Boolean,
        sessionId: String,
        offline: Boolean
    ): Map<String, String>

    suspend fun getSession(): Map<String, String>

    suspend fun exitSession()

    suspend fun getListLesson(): List<Map<String, String>>

    suspend fun updateSchoolData(): Map<String, String>

    suspend fun getLessonDetail(lessonId: Int): Map<String, String>

    fun checkUpdate(): LiveData<String>

    suspend fun getListReport(): List<Map<String, String>>
}