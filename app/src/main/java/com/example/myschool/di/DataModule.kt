package com.example.myschool.di

import com.example.myschool.data.domain.repository.DomainRepository
import com.example.myschool.data.domain.repository.DomainRepositoryImpl
import com.example.myschool.data.network.NetworkRepository
import com.example.myschool.data.network.NetworkRepositoryImpl
import com.example.myschool.data.storage.StorageRepository
import com.example.myschool.data.storage.StorageRepositoryImpl
import org.koin.dsl.module

val dataModule = module {

    single<DomainRepository> {
        DomainRepositoryImpl(
            networkRepository = get(),
            storageRepository = get(),
            lessonDao = get()
        )
    }

    single<NetworkRepository> {
        NetworkRepositoryImpl()
    }

    single<StorageRepository> {
        StorageRepositoryImpl(context = get())
    }
}