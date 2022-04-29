package com.example.myschool.presentation.fragment.lesson

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschool.data.domain.usecase.lesson.UCCheckUpdate
import com.example.myschool.data.domain.usecase.lesson.UseCaseGetListLesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelLesson(
    private val useCaseGetListLesson: UseCaseGetListLesson,
    private val ucCheckUpdate: UCCheckUpdate
) : ViewModel() {

    private var lessonLive = MutableLiveData<List<ModelLesson>>()
    var lesson: LiveData<List<ModelLesson>> = lessonLive

    init {
        viewModelScope.launch {
            ucCheckUpdate.execute().observeForever {
                getListLesson()
            }
        }
    }

    private fun getListLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            val listLesson = mutableListOf<ModelLesson>()

            useCaseGetListLesson.execute().forEach {
                listLesson.add(
                    ModelLesson(
                        id = "${it["id"]}",
                        name = "${it["name"]}",
                        grade = "${it["grade"]}",
                        average = "${it["average"]}",
                        info = "${it["info"]}"
                    )
                )
            }

            lessonLive.postValue(listLesson)
        }
    }
}