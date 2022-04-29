package com.example.myschool.presentation.fragment.lesson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschool.data.domain.usecase.lesson.UseCaseGetLessonDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelLessonDetail(
    private val useCaseGetLessonDetail: UseCaseGetLessonDetail
) : ViewModel() {

    private var lessonDetailLive = MutableLiveData<Map<String, String>>()
    var lessonDetail: LiveData<Map<String, String>> = lessonDetailLive

    fun getLessonDetail(lessonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = useCaseGetLessonDetail.execute(lessonId)

            lessonDetailLive.postValue(response)
        }
    }
}