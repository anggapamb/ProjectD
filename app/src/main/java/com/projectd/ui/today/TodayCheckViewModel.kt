package com.projectd.ui.today

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.Cons
import com.projectd.data.model.Absent
import com.projectd.data.model.Task
import com.projectd.data.model.User
import com.projectd.ui.task.add.TaskAddFragment
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
                    val status = if (approved == "true") { "approved" } else { "rejected" }
                    apiService.notificationAbsent(idAbsent, status)
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

    private fun customData(status: String, tasks: List<Task>): List<Task?> {
        val filterTasks = ArrayList<Task?>(tasks)
        when (status) {
            "Standby" -> {
                return if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.load?.contains(Task.STANDBY, true) == true }
                } else {
                    val taskStandby = filterTasks.filter { it?.load?.contains(Task.STANDBY, true) == true }
                    taskStandby.filter { it?.devision?.contains(user?.devision.toString(), true) == true}
                }

            }
            "Done" -> {
                return if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.status?.contains(Task.DONE, true) == true }
                } else {
                    val taskDone = filterTasks.filter { it?.status?.contains(Task.DONE, true) == true }
                    taskDone.filter { it?.devision?.contains(user?.devision.toString(), true) == true}
                }
            }
            "Cancel" -> {
                return if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.status?.contains(Task.CANCEL, true) == true }
                } else {
                    val taskCancel = filterTasks.filter { it?.status?.contains(Task.CANCEL, true) == true }
                    taskCancel.filter { it?.devision?.contains(user?.devision.toString(), true) == true}
                }
            }
            "On-going" -> {
                val onGoing = filterTasks.filter { it?.status?.contains(Task.HOLD, true) == true || it?.status.equals(null) }
                val list = ArrayList<Task?>()
                ArrayList(onGoing).forEach {
                    if (it?.load != TaskAddFragment.Companion.LOAD.STANDBY) {
                        list.add(it)
                    }
                }

                return if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    list
                } else {
                    list.filter { it?.devision?.contains(user?.devision.toString(), true) == true }
                }
            }
         }

        return filterTasks
    }

    fun verifyTask(idTask: String?, onResponse: () -> Unit) = viewModelScope.launch {
        ApiObserver(
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

    private val _dataUsers: Channel<List<User?>> = Channel()
    val dataUsers = _dataUsers.receiveAsFlow()

    fun userNotReady() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.userNotReady()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<User>(gson)
                    _dataUsers.send(customUserNotReady(data))
                }

                override suspend fun onError(response: ApiResponse) {
                    val jsonResponse = response.rawResponse?.let { JSONObject(it) }
                    val data = jsonResponse?.getJSONArray("data")?.toList<User>(gson)
                    if (data != null) {
                        _dataUsers.send(customUserNotReady(data))
                    } else {
                        _dataUsers.send(customUserNotReady(emptyList()))
                    }
                }
            }
        )
    }

    private fun customUserNotReady(data: List<User>): List<User?> {
        val dataUsers = ArrayList<User?>(data)

        if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
            return dataUsers
        } else if (user?.isLeader == true) {
            return dataUsers.filter { it?.devision?.id?.toString()?.contains(user?.devision?.id.toString(), true) == true }
        }

        return dataUsers
    }

}