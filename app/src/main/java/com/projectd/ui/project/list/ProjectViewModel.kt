package com.projectd.ui.project.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.crocodic.core.base.adapter.CorePagingSource
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.paging.ProjectRepository
import com.projectd.paging.ProjectRepositoryImpl
import kotlinx.coroutines.flow.first

class ProjectViewModel(apiService: ApiService): BaseViewModel() {

    private val repository: ProjectRepository = ProjectRepositoryImpl(apiService, gson)
    private val firstPage = 1
    private val itemPerPage = 10

    fun projectList() = Pager(CorePagingSource.config(itemPerPage), pagingSourceFactory = {
        CorePagingSource(firstPage) { page, limit ->
            repository.getAllProject(page, limit).first() // List<Product>
        }
    }).flow.cachedIn(viewModelScope)

}