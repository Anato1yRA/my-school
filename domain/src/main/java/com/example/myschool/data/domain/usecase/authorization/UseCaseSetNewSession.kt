package com.example.myschool.data.domain.usecase.authorization

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseSetNewSession(private val domainRepository: DomainRepository) {

    suspend fun execute(
        login: String,
        password: String,
        gu: Boolean,
        sessionId: String,
        offline: Boolean
    ): Map<String, String> {
        return domainRepository.setNewSession(
            login = login,
            password = password,
            gu = gu,
            sessionId = sessionId,
            offline = offline
        )
    }
}