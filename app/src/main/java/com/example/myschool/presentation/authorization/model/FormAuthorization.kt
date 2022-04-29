package com.example.myschool.presentation.authorization.model

data class FormAuthorization(
    val login: String,
    val password: String,
    val gu: Boolean
)
