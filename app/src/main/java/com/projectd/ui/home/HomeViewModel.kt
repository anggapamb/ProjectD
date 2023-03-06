package com.projectd.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.Cons
import com.projectd.data.model.Absent
import com.projectd.data.model.AdditionalMenu
import com.projectd.data.model.Prayer
import com.projectd.data.model.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(private val apiService: ApiService, private val observer: BaseObserver) : BaseViewModel() {

    private val _dataTasks: Channel<List<Task?>> = Channel()
    val dataTasks =_dataTasks.receiveAsFlow()

    fun taskToday() = viewModelScope.launch {
        observer(
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

    private val _dataMenus: Channel<List<AdditionalMenu?>> = Channel()
    val dataMenus = _dataMenus.receiveAsFlow()

    fun allMenus() = viewModelScope.launch {
        observer(
            block = {apiService.addMenus()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<AdditionalMenu>(gson)
                    _dataMenus.send(customData(data))
                }

                override suspend fun onError(response: ApiResponse) {
                    _dataMenus.send(emptyList())
                }

            }
        )
    }

    private fun customData(data: List<AdditionalMenu>): List<AdditionalMenu?> {
        val todayCheck = data.single { it.key?.contains("today_check", true) == true }
        val menus = ArrayList<AdditionalMenu>(data)

        if (user?.devision?.id == Cons.DIVISION.MANAGER || user?.devision?.id == Cons.DIVISION.PSDM || user?.devision?.id == Cons.DIVISION.SUPER_ADMIN) {
            return menus
        } else if (user?.isLeader == true){
            return menus
        } else {
            menus.remove(todayCheck)
        }

       return menus
    }

    private val _dataAbsent: Channel<Absent?> = Channel()
    val dataAbsent = _dataAbsent.receiveAsFlow()

    fun getAbsent() = viewModelScope.launch {
        observer(
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

    private val _prayer: Channel<Prayer> = Channel()
    val prayer = _prayer.receiveAsFlow()

    fun preparePrayer() = viewModelScope.launch {
        observer(
            block = {apiService.showPrayer()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject("data").toObject<Prayer>(gson)
                    _prayer.send(data)
                }

            }
        )
    }

}