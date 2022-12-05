package com.projectd.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Absent
import com.projectd.data.model.AdditionalMenu
import com.projectd.data.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(private val apiService: ApiService) : BaseViewModel() {

    private val _dataTasks: Channel<List<Task?>> = Channel()
    val dataTasks =_dataTasks.receiveAsFlow()

    fun taskToday() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.taskToday()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Task>(gson)
                    _dataTasks.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _dataTasks.send(emptyList())
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

    private val _dataMenus: Channel<List<AdditionalMenu?>> = Channel()
    val dataMenus = _dataMenus.receiveAsFlow()

    fun allMenus() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.addMenus()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<AdditionalMenu>(gson)
                    _dataMenus.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataMenus.send(emptyList())
                }

            }
        )
    }

    private val _dataAbsent: Channel<Absent?> = Channel()
    val dataAbsent = _dataAbsent.receiveAsFlow()

    fun getAbsent() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.getAbsent()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").getJSONObject(0).toObject<Absent>(gson)
                    _dataAbsent.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataAbsent.send(null)
                }

            }
        )
    }

}