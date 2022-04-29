package com.example.myschool.data.domain.usecase.report

import com.example.myschool.data.domain.repository.DomainRepository

class UCGetListReport(private val domainRepository: DomainRepository) {

    suspend fun execute(): List<Map<String, String>> {
        return domainRepository.getListReport()
    }
}