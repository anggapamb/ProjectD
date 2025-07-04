package com.projectd.ui.today

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.Cons
import com.projectd.data.model.*
import com.projectd.ui.task.add.TaskAddFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

class TodayCheckViewModel(private val apiService: ApiService, private val observer: BaseObserver): BaseViewModel() {

    private val _dataAbsents: Channel<List<AllAbsent?>> = Channel()
    val dataAbsents = _dataAbsents.receiveAsFlow()

    fun listAllAbsent() = viewModelScope.launch {
        observer(
            block = {apiService.getListAllAbsent()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<AllAbsent>(gson)
                    _dataAbsents.send(customDataAbsent(data))
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataAbsents.send(emptyList())
                }

            }
        )
    }

    private fun customDataAbsent(data: List<AllAbsent>) : List<AllAbsent?> {
        val dataUsers = ArrayList<AllAbsent?>(data)

        if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
            return dataUsers
        } else if (session.getUser()?.isLeader == true) {
            val dataSend = ArrayList<AllAbsent?>()
            dataUsers.forEach {
                if (it?.detailUserAbsent?.idDevision == session.getUser()?.devision?.id) {
                    dataSend.add(it)
                }
            }
            return dataSend
        }

        return dataUsers
    }

    fun approvedAbsent(idAbsent: String, approved: Boolean, onResponse: () -> Unit) = viewModelScope.launch {
        observer(
            block = {apiService.approvedAbsent(idAbsent, approved)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    onResponse.invoke()
                    //val status = if (approved) { "approved" } else { "rejected" }
                    //apiService.notificationAbsent(idAbsent, status)
                }

                override suspend fun onError(response: ApiResponse) {
                    onResponse.invoke()
                }

            }
        )
    }

    private val _dataTasks: Channel<List<Task?>> = Channel()
    val dataTasks =_dataTasks.receiveAsFlow()

    fun taskToday(status: String) = viewModelScope.launch {
        observer(
            block = {apiService.taskToday()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Task>(gson)
                    _dataTasks.send(customData(status, data))
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataTasks.send(emptyList())
                }

            }
        )
    }

    private fun customData(status: String, tasks: List<Task>): List<Task?> {
        val filterTasks = ArrayList<Task?>(tasks)
        when (status) {
            "Standby" -> {
                return if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.load?.contains(Task.STANDBY, true) == true }
                } else {
                    val taskStandby = filterTasks.filter { it?.load?.contains(Task.STANDBY, true) == true }
                    val dataSend = ArrayList<Task?>()
                    taskStandby.forEach {
                        if (it?.createdBy?.devision?.id == session.getUser()?.devision?.id) {
                            dataSend.add(it)
                        }
                    }
                    dataSend
                }

            }
            "Done" -> {
                return if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.status?.contains(Task.DONE, true) == true }
                } else {
                    val taskDone = filterTasks.filter { it?.status?.contains(Task.DONE, true) == true }
                    val dataSend = ArrayList<Task?>()
                    taskDone.forEach {
                        if (it?.createdBy?.devision?.id == session.getUser()?.devision?.id) {
                            dataSend.add(it)
                        }
                    }
                    dataSend
                }
            }
            "Cancel" -> {
                return if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    filterTasks.filter { it?.status?.contains(Task.CANCEL, true) == true }
                } else {
                    val taskCancel = filterTasks.filter { it?.status?.contains(Task.CANCEL, true) == true }
                    val dataSend = ArrayList<Task?>()
                    taskCancel.forEach {
                        if (it?.createdBy?.devision?.id == session.getUser()?.devision?.id) {
                            dataSend.add(it)
                        }
                    }
                    dataSend
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

                return if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
                    list
                } else {
                    val dataSend = ArrayList<Task?>()
                    list.forEach {
                        if (it?.createdBy?.devision?.id == session.getUser()?.devision?.id) {
                            dataSend.add(it)
                        }
                    }
                    dataSend
                }
            }
         }

        return filterTasks
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

    private val _dataUsers: Channel<List<UserNotReady?>> = Channel()
    val dataUsers = _dataUsers.receiveAsFlow()

    fun userNotReady() = viewModelScope.launch {
        observer(
            block = {apiService.userNotReady()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<UserNotReady>(gson)
                    _dataUsers.send(customUserNotReady(data))
                }

                override suspend fun onError(response: ApiResponse) {
                    val jsonResponse = response.rawResponse?.let { JSONObject(it) }
                    val data = jsonResponse?.getJSONArray("data")?.toList<UserNotReady>(gson)
                    if (data != null) {
                        _dataUsers.send(customUserNotReady(data))
                    } else {
                        _dataUsers.send(customUserNotReady(emptyList()))
                    }
                }
            }
        )
    }

    private fun customUserNotReady(data: List<UserNotReady>): List<UserNotReady?> {
        val dataUsers = ArrayList<UserNotReady?>(data)

        if (session.getUser()?.devision?.id == Cons.DIVISION.MANAGER || session.getUser()?.devision?.id == Cons.DIVISION.PSDM || session.getUser()?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
            return dataUsers
        } else if (session.getUser()?.isLeader == true) {
            val dataSend = ArrayList<UserNotReady?>()
            dataUsers.forEach {
                if (it?.idDevision == session.getUser()?.devision?.id) {
                    dataSend.add(it)
                }
            }
            return dataSend
        }

        return dataUsers
    }

}