package com.example.myschool.data.domain.usecase.service

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseUpdateSchoolData(private val domainRepository: DomainRepository) {

    suspend fun execute(): Map<String, String> {
        return domainRepository.updateSchoolData()
    }
}