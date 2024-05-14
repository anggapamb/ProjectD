package com.projectd.ui.task.list

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.TaskByDate
import com.projectd.data.model.TaskDay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class TaskViewModel(private val apiService: ApiService, private val observer: BaseObserver): BaseViewModel() {

    fun generate30Days(): List<TaskDay?> {
        val days = ArrayList<TaskDay?>()

        val today = Calendar.getInstance().apply {
            add(Calendar.DATE, -15)
        }

        val maxDay = (today.clone() as Calendar).apply {
            add(Calendar.DATE, 30)
        }

        while (today.before(maxDay)) {
            days.add(TaskDay(DateTimeHelper().fromDate(today.time), DateTimeHelper().fromDate(today.time) == DateTimeHelper().dateNow()))
            today.add(Calendar.DATE, 1)
        }

        return days
    }

    private val _dataTasks: Channel<List<TaskByDate>> = Channel()
    val dataTasks = _dataTasks.receiveAsFlow()

    fun taskByDate(date: String = DateTimeHelper().dateNow()) = viewModelScope.launch {
        observer(
            block = {apiService.taskByDate(date)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<TaskByDate>(gson)
                    _dataTasks.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataTasks.send(emptyList())
                }

            }
        )
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