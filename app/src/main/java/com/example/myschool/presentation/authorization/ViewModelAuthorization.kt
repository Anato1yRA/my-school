package com.example.myschool.presentation.authorization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschool.data.domain.usecase.authorization.UseCaseGetSession
import com.example.myschool.data.domain.usecase.authorization.UseCaseSetNewSession
import com.example.myschool.presentation.authorization.model.FormAuthorization
import kotlinx.coroutines.launch

class ViewModelAuthorization(
    private val useCaseSetNewSession: UseCaseSetNewSession,
    useCaseGetSession: UseCaseGetSession
) : ViewModel() {

    private var progressBarLive = MutableLiveData<Boolean>()
    var progressBar: LiveData<Boolean> = progressBarLive

    private var errorMessageLive = MutableLiveData<String>()
    var errorMessage: LiveData<String> = errorMessageLive

    private var isAuthorizationLive = MutableLiveData<Boolean>()
    var isAuthorization: LiveData<Boolean> = isAuthorizationLive

    private var formAuthorizationLive = MutableLiveData<FormAuthorization>()
    var formAuthorization: LiveData<FormAuthorization> = formAuthorizationLive

    init {
        progressBarLive.value = false
        errorMessageLive.value = ""

        viewModelScope.launch {
            val response = useCaseGetSession.execute()

            formAuthorizationLive.value = FormAuthorization(
                login = "${response["login"]}",
                password = "${response["password"]}",
                gu = response["gu"].toBoolean()
            )

            if (response["offline"] == "1" || response["sessionId"] != "") {
                isAuthorizationLive.postValue(true)
            } else {
                isAuthorizationLive.postValue(false)
            }
        }
    }

    fun progressBarStart() {
        progressBarLive.value = true
    }

    fun progressBarStop() {
        progressBarLive.value = false
    }

    fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            errorMessageLive.postValue(message)
        }
    }

    fun setNewSession(
        login: String,
        password: String,
        gu: Boolean,
        sessionId: String,
        offline: Boolean
    ) {
        viewModelScope.launch {
            val response = useCaseSetNewSession.execute(
                login = login,
                password = password,
                gu = gu,
                sessionId = sessionId,
                offline = offline
            )

            if (response["responseCode"] == "200") {
                isAuthorizationLive.postValue(true)
            }

            sendMessage(response["responseMessage"].toString())

            progressBarStop()
        }
    }
}