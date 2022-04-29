package com.example.myschool.data.storage

interface StorageRepository {

    suspend fun set(login: String, password: String, gu: Boolean, sessionId: String, offline: Boolean)

    suspend fun get(): Map<String, String>

    suspend fun getUserLogin(): String

    suspend fun exit()

    suspend fun setOffline()

    suspend fun setOnline()
}