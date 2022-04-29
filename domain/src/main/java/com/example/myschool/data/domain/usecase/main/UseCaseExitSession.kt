package com.example.myschool.data.domain.usecase.main

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseExitSession(private val domainRepository: DomainRepository) {

    suspend fun execute() {
        domainRepository.exitSession()
    }
}