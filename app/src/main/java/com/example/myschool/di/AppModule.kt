package com.example.myschool.di

import com.example.myschool.data.domain.usecase.lesson.UCCheckUpdate
import com.example.myschool.presentation.authorization.ViewModelAuthorization
import com.example.myschool.presentation.fragment.lesson.ViewModelLesson
import com.example.myschool.presentation.fragment.lesson.ViewModelLessonDetail
import com.example.myschool.presentation.fragment.report.VMReport
import com.example.myschool.presentation.main.ViewModelMain
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<ViewModelAuthorization> {
        ViewModelAuthorization(
            useCaseSetNewSession = get(),
            useCaseGetSession = get()
        )
    }

    viewModel<ViewModelMain> {
        ViewModelMain(
            useCaseExitSession = get(),
            useCaseGetSession = get(),
            ucCheckUpdate = get()
        )
    }

    viewModel<ViewModelLesson> {
        ViewModelLesson(
            useCaseGetListLesson = get(),
            ucCheckUpdate = get()
        )
    }

    viewModel<ViewModelLessonDetail> {
        ViewModelLessonDetail(
            useCaseGetLessonDetail = get()
        )
    }

    viewModel<VMReport> {
        VMReport(
            ucGetListReport = get(),
            ucCheckUpdate = get()
        )
    }
}