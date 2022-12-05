package com.projectd.ui.project.list

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProjectViewModel(private val apiService: ApiService): BaseViewModel() {

    private val _dataProjects: Channel<List<Project?>> = Channel()
    val dataProjects = _dataProjects.receiveAsFlow()

    fun allProject() = viewModelScope.launch {
        ApiObserver(
            block = {apiService.allProject()},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<Project>(gson)
                    _dataProjects.send(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _dataProjects.send(emptyList())
                }
            }

        )
    }

}