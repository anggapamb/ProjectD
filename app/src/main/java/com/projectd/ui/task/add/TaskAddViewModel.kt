package com.projectd.ui.task.add

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class TaskAddViewModel(private val apiService: ApiService): BaseViewModel() {

    fun addTask(task: String?, project: String?, startD: String?, endD: String?, load: String?, createBy: String?, photo: String) = viewModelScope.launch {
        ApiObserver(
            block = {apiService.addTask(task, project, startD, endD, load, createBy, photo)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val msg = response.getString("message")
                    _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = msg))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse(ApiStatus.ERROR))
                }

            }
        )
    }
}