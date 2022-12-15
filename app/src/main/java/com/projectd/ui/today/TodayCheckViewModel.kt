package com.projectd.ui.today

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Absent
import com.projectd.data.model.Task
import com.projectd.data.model.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class TodayCheckViewModel(private val apiService: ApiService): BaseViewModel() {

    private val _dataAbsents: Channel<List<Absent?>> = Channel()
    val dataAbsents = _dataAbsents.receiveAsFlow()

    fun listAllAbsent() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.getListAllAbsent()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Absent>(gson)
                    _dataAbsents.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataAbsents.send(emptyList())
                }

            }
        )
    }

    fun approvedAbsent(idAbsent: String, approved: String, onResponse: () -> Unit) = viewModelScope.launch {
        ApiObserver(
            block = {apiService.approvedAbsent(idAbsent, approved)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    onResponse.invoke()
                }

            }
        )
    }

    private val _dataTasks: Channel<List<Task?>> = Channel()
    val dataTasks =_dataTasks.receiveAsFlow()

    fun taskToday(status: String) = viewModelScope.launch {
        ApiObserver(
            block = {apiService.taskToday()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Task>(gson)
                    _dataTasks.send(customData(status, data))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _dataTasks.send(emptyList())
                }

            }
        )
    }

    private fun customData(status: String, tasks: List<Task>): List<Task> {
         return when (status) {
            "Standby" -> {
                tasks.filter { it.load?.contains(Task.STANDBY, true) == true }
            }
            "Done" -> {
                tasks.filter { it.status?.contains(Task.DONE, true) == true }
            }
            "Cancel" -> {
                tasks.filter { it.status?.contains(Task.CANCEL, true) == true }
            }
            "On-going" -> {
                tasks.filter { it.status?.contains(Task.HOLD, true) == true || it.status.equals(null) }
                // TODO: remove yang loadnya standby
            }
             else -> { tasks }
         }
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

    private val _dataUsers: Channel<List<User?>> = Channel()
    val dataUsers = _dataUsers.receiveAsFlow()

    fun userNotReady() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.userNotReady()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<User>(gson)
                    _dataUsers.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _dataUsers.send(emptyList())
                }
            }
        )
    }

}