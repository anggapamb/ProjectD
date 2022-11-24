package com.projectd.ui.project.add

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProjectAddViewModel(private val apiService: ApiService): BaseViewModel() {

    fun addProject(
        projectName: String?,
        description: String?,
        startDate: String?,
        endDate: String?,
        projectDirector: String?,
        difficult: String?) = viewModelScope.launch {

            ApiObserver(
                block = {apiService.addProject(projectName, description, startDate, endDate, projectDirector, difficult)},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.send(ApiResponse(ApiStatus.SUCCESS))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.send(ApiResponse(ApiStatus.ERROR))
                    }
                }

            )
    }

}