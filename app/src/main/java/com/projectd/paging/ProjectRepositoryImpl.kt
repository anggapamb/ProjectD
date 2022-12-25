package com.projectd.paging

import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.projectd.api.ApiService
import com.projectd.data.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class ProjectRepositoryImpl(private val apiService: ApiService, private val gson: Gson): ProjectRepository {

    override fun getAllProject(page: Int, limit: Int): Flow<List<Project>> = flow {
        val project = apiService.allProject(page, limit)
        val projectList = JSONObject(project).getJSONArray("data").toList<Project>(gson)
        emit(projectList)
    }

    override fun searchProject(projectName: String?, page: Int?, limit: Int?): Flow<List<Project>> = flow {
        val project = apiService.searchProject(projectName, page, limit)
        val projectList = JSONObject(project).getJSONArray("data").toList<Project>(gson)
        emit(projectList)
    }
}