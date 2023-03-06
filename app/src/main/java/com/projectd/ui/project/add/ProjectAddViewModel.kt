package com.projectd.ui.project.add

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProjectAddViewModel(private val apiService: ApiService, private val observe: BaseObserver): BaseViewModel() {

    fun addProject(
        idProject: String?,
        projectName: String?,
        description: String?,
        startDate: String?,
        endDate: String?,
        projectDirector: String?,
        difficult: String?,
        createdBy: String?,
        progress: String?) = viewModelScope.launch {

        if (projectName.isNullOrEmpty() || description.isNullOrEmpty() || startDate.isNullOrEmpty() || endDate.isNullOrEmpty() || projectDirector.isNullOrEmpty()
            || difficult.isNullOrEmpty()) {
            _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
        } else {
            _apiResponse.send(ApiResponse(ApiStatus.LOADING, message = "Submitting..."))
            observe(
                block = {
                        if (idProject.isNullOrEmpty()) {
                            apiService.addProject(projectName, description, startDate, endDate, projectDirector, difficult)
                        } else {
                            apiService.updateProject(idProject,projectName, description, startDate, endDate, projectDirector, difficult)
                        }
                },
                toast = true,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val message = response.getString("message")
                        _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))
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