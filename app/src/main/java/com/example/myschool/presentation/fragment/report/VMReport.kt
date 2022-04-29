package com.example.myschool.presentation.fragment.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschool.data.domain.usecase.lesson.UCCheckUpdate
import com.example.myschool.data.domain.usecase.report.UCGetListReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VMReport(
    private val ucGetListReport: UCGetListReport,
    private val ucCheckUpdate: UCCheckUpdate
) : ViewModel() {

    private var _report = MutableLiveData<List<Map<String, String>>>()
    var report: LiveData<List<Map<String, String>>> = _report

    init {
        viewModelScope.launch {
            ucCheckUpdate.execute().observeForever {
                getListReport()
            }
        }
    }


    private fun getListReport() {
        viewModelScope.launch(Dispatchers.IO) {
            val reportList = mutableListOf<Map<String, String>>()

            ucGetListReport.execute().forEach {
                reportList.add(it)
            }

            _report.postValue(reportList)
        }
    }
}