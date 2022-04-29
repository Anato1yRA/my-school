package com.example.myschool.data.storage

import android.content.Context

class StorageRepositoryImpl(context: Context) : StorageRepository {

    private val sharedPreferences =
        context.getSharedPreferences("storage_session", Context.MODE_PRIVATE)

    override suspend fun set(login: String, password: String, gu: Boolean, sessionId: String, offline: Boolean) {
        sharedPreferences.edit().putString("login", login).apply()
        sharedPreferences.edit().putString("password", password).apply()
        sharedPreferences.edit().putString("gu", gu.toString()).apply()
        sharedPreferences.edit().putString("sessionId", sessionId).apply()
        sharedPreferences.edit().putString("offline", offline.toString()).apply()
    }

    override suspend fun get(): Map<String, String> {
        val login = sharedPreferences.getString("login", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        val gu = sharedPreferences.getString("gu", "1") ?: "1"
        val sessionId = sharedPreferences.getString("sessionId", "") ?: ""
        val offline = sharedPreferences.getString("offline", "0") ?: "0"

        return mapOf(
            "login" to login,
            "password" to password,
            "gu" to gu,
            "sessionId" to sessionId,
            "offline" to offline
        )
    }

    override suspend fun getUserLogin(): String {
        return sharedPreferences.getString("login", "") ?: ""
    }

    override suspend fun exit() {
        sharedPreferences.edit().putString("sessionId", "").apply()
        sharedPreferences.edit().putString("offline", "0").apply()
    }

    override suspend fun setOffline() {
        sharedPreferences.edit().putString("offline", "1").apply()
    }

    override suspend fun setOnline() {
        sharedPreferences.edit().putString("offline", "0").apply()
    }
}