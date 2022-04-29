package com.example.myschool.di

import android.app.Application
import com.example.myschool.data.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppKoin : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@AppKoin)
            modules(
                listOf(
                    appModule,
                    domainModule,
                    dataModule,
                    roomModule
                )
            )
        }
    }
}