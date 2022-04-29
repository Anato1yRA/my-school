package com.example.myschool.di

import com.example.myschool.data.domain.usecase.authorization.UseCaseGetSession
import com.example.myschool.data.domain.usecase.authorization.UseCaseSetNewSession
import com.example.myschool.data.domain.usecase.lesson.UCCheckUpdate
import com.example.myschool.data.domain.usecase.lesson.UseCaseGetLessonDetail
import com.example.myschool.data.domain.usecase.lesson.UseCaseGetListLesson
import com.example.myschool.data.domain.usecase.main.UseCaseExitSession
import com.example.myschool.data.domain.usecase.report.UCGetListReport
import com.example.myschool.data.domain.usecase.service.UseCaseUpdateSchoolData
import org.koin.dsl.module

val domainModule = module {

    factory<UseCaseSetNewSession> {
        UseCaseSetNewSession(domainRepository = get())
    }

    factory<UseCaseGetSession> {
        UseCaseGetSession(domainRepository = get())
    }

    factory<UseCaseExitSession> {
        UseCaseExitSession(domainRepository = get())
    }

    factory<UseCaseGetListLesson> {
        UseCaseGetListLesson(domainRepository = get())
    }

    factory<UseCaseUpdateSchoolData> {
        UseCaseUpdateSchoolData(domainRepository = get())
    }

    factory<UseCaseGetLessonDetail> {
        UseCaseGetLessonDetail(domainRepository = get())
    }

    factory<UCCheckUpdate> {
        UCCheckUpdate(domainRepository = get())
    }

    factory<UCGetListReport> {
        UCGetListReport(domainRepository = get())
    }
}