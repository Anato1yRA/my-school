package com.example.myschool.data.domain.usecase.authorization

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseGetSession(private val domainRepository: DomainRepository) {

    suspend fun execute(): Map<String, String> {
        return domainRepository.getSession()
    }
}