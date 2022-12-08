package com.projectd.ui.project.list

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.crocodic.core.base.adapter.CorePagingSource
import com.crocodic.core.extension.toList
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import org.json.JSONObject

class ProjectViewModel(private val apiService: ApiService): BaseViewModel() {

    private val firstPage = 1
    private val itemPerPage = 10

    fun allProject() = Pager(CorePagingSource.config(itemPerPage), pagingSourceFactory = {
        CorePagingSource(firstPage){ page, _ ->
            val data = JSONObject(apiService.allProject(page)).getJSONArray("data").toList<Project>(gson)
            data // List<Product>
        }
    }).flow.cachedIn(viewModelScope)

}