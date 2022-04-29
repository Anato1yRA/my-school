package com.example.myschool.data.domain.usecase.lesson

import com.example.myschool.data.domain.repository.DomainRepository

class UseCaseGetLessonDetail(private val domainRepository: DomainRepository) {

    suspend fun execute(lessonId: Int): Map<String, String> {
        return domainRepository.getLessonDetail(lessonId)
    }
}