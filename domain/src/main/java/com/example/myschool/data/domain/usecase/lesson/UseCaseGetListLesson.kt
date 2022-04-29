package com.example.myschool.data.domain.usecase.lesson

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseGetListLesson(private val domainRepository: DomainRepository) {

    suspend fun execute(): List<Map<String, String>> {
        return domainRepository.getListLesson()
    }
}