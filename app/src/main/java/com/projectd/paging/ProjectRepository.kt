package com.projectd.paging

import com.projectd.data.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProject(page: Int, limit: Int): Flow<List<Project>>
    fun searchProject(projectName: String?,page: Int?, limit: Int?): Flow<List<Project>>
}