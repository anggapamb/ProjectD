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

    fun addTask(task: String?, project: String?, startD: String?, endD: String?, load: String?, createBy: String?, photo: String?) = viewModelScope.launch {

        if (load == TaskAddFragment.Companion.LOAD.STANDBY) {
            if (task.isNullOrEmpty() || load.isEmpty() || createBy.isNullOrEmpty() || photo.isNullOrEmpty()) {
                _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
            } else {
                _apiResponse.send(ApiResponse(ApiStatus.LOADING, message = "Submitting..."))
                ApiObserver(
                    block = {apiService.addTask(task, "null", "null", "null", load, createBy, photo)},
                    toast = false,
                    responseListener = object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            val msg = response.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = msg))
                        }

                        override suspend fun onError(response: ApiResponse) {
                            val message = response.rawResponse?.let { JSONObject(it) }?.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = message))
                        }

                    }
                )
            }
        } else {
            if (task.isNullOrEmpty() || project.isNullOrEmpty() || startD.isNullOrEmpty() || endD.isNullOrEmpty() || load.isNullOrEmpty()
                || createBy.isNullOrEmpty() || photo.isNullOrEmpty()) {
                _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
            } else {
                _apiResponse.send(ApiResponse(ApiStatus.LOADING, message = "Submitting..."))
                ApiObserver(
                    block = {apiService.addTask(task, project, startD, endD, load, createBy, photo)},
                    toast = false,
                    responseListener = object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            val msg = response.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = msg))
                        }

                        override suspend fun onError(response: ApiResponse) {
                            val message = response.rawResponse?.let { JSONObject(it) }?.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = message))
                        }

                    }
                )
            }
        }
    }
}