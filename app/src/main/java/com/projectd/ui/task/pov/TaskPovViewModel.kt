package com.projectd.ui.task.pov

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

class TaskPovViewModel(private val apiService: ApiService, private val observer: BaseObserver): BaseViewModel() {

    private val _dataTasks: Channel<List<Task?>> = Channel()
    val dataTasks =_dataTasks.receiveAsFlow()

    fun taskToday(idLogin: String?) = viewModelScope.launch {
        observer(
            block = {apiService.taskToday()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Task>(gson)
                    _dataTasks.send(customTask(data, idLogin))
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataTasks.send(emptyList())
                }

            }
        )
    }

    private fun customTask(data: List<Task>, idLogin: String?): List<Task?> {
        val tasks = ArrayList<Task>(data)
        val dataFilter = ArrayList<Task>()
        tasks.forEach {
            if (it.createdBy?.id.toString() == idLogin.toString()) {
                dataFilter.add(it)
            }
        }
        return dataFilter
    }

    fun verifyTask(idTask: String?, onResponse: () -> Unit) = viewModelScope.launch {
        observer(
            block = {apiService.verifyTask(idTask)},
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

}