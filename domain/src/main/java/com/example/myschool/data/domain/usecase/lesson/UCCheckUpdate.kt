package com.example.myschool.data.domain.usecase.lesson

import androidx.lifecycle.LiveData
import com.example.myschool.data.domain.repository.DomainRepository

class UCCheckUpdate(private val domainRepository: DomainRepository) {
    fun execute(): LiveData<String> {
        return domainRepository.checkUpdate()
    }
}