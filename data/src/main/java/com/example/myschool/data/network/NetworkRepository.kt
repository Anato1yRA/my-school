package com.example.myschool.data.network

interface NetworkRepository {

    suspend fun getRequestData(parameters: Map<String, String>): Map<String, String>
}