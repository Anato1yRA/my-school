package com.example.myschool.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschool.data.domain.usecase.authorization.UseCaseGetSession
import com.example.myschool.data.domain.usecase.lesson.UCCheckUpdate
import com.example.myschool.data.domain.usecase.main.UseCaseExitSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelMain(
    private val useCaseExitSession: UseCaseExitSession,
    private val useCaseGetSession: UseCaseGetSession,
    private val ucCheckUpdate: UCCheckUpdate
) : ViewModel() {

    private var isAuthorizationLive = MutableLiveData<Boolean>()
    var isAuthorization: LiveData<Boolean> = isAuthorizationLive

    private var isOfflineLive = MutableLiveData<Boolean>()
    var isOffline: LiveData<Boolean> = isOfflineLive

    init {
        viewModelScope.launch(Dispatchers.Main) {
            ucCheckUpdate.execute().observeForever {
                getData()
            }
        }
    }

    private fun getData(){
        viewModelScope.launch {
            val response = useCaseGetSession.execute()

            if(response["offline"] == "0"){
                isOfflineLive.postValue(false)
            }else{
                isOfflineLive.postValue(true)
            }

            if (response["offline"] == "0" && response["sessionId"] == "") {
                isAuthorizationLive.postValue(false)
            } else {
                isAuthorizationLive.postValue(true)
            }
        }
    }

    fun exit() {
        viewModelScope.launch {
            useCaseExitSession.execute()

            isOfflineLive.value = false
            isAuthorizationLive.value = false
        }
    }
}