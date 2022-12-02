package com.projectd.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.AdditionalMenu
import com.projectd.data.model.Task
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(private val apiService: ApiService) : BaseViewModel() {

    val listTask = MutableLiveData<List<Task?>>()
    val dataMenus = MutableLiveData<List<AdditionalMenu>>()

    fun taskToday() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.taskToday()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Task>(gson)
                    listTask.postValue(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    listTask.postValue(emptyList())
                }

            }
        )
    }

    fun verifyTask(idTask: String?, token: String?, onResponse: () -> Unit) = viewModelScope.launch {
        ApiObserver(
            block = {apiService.verifyTask(idTask, token)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    onResponse.invoke()
                }

                override suspend fun onError(response: ApiResponse) {
                    onResponse.invoke()
                }

            }
        )
    }

    fun allMenus() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.addMenus()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<AdditionalMenu>(gson)
                    dataMenus.postValue(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    dataMenus.postValue(emptyList())
                }

            }
        )
    }

}