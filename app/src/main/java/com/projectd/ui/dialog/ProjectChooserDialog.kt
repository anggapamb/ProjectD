package com.projectd.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.base.adapter.CorePagingSource
import com.crocodic.core.base.adapter.PaginationAdapter
import com.crocodic.core.base.adapter.PaginationLoadState
import com.crocodic.core.extension.toList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import com.projectd.databinding.DialogProjectChooserBinding
import com.projectd.databinding.ItemProjectChooserBinding
import com.projectd.databinding.StateLoadingBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectChooserDialog(private val onSelect: (Project?) -> Unit, private val onCancel: () -> Unit, private val addProject: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogProjectChooserBinding? = null
    private val viewModel: ProjectChooserViewModel by viewModel()
    private val adapter = PaginationAdapter<ItemProjectChooserBinding, Project>(R.layout.item_project_chooser)
        .initItem { _, data ->
            onSelect(data)
            this@ProjectChooserDialog.dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_project_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
        viewModel.allProject()
    }

    private fun initView() {
        with(adapter) {
            val loadStateFooter = PaginationLoadState<StateLoadingBinding>(R.layout.state_loading)
            binding?.rvProject?.adapter = withLoadStateFooter(loadStateFooter)
        }

        binding?.btnAddProject?.setOnClickListener {
            addProject()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            launch {
                viewModel.allProject().collect {
                    adapter.submitData(it)
                }
            }
            launch {
                adapter.loadStateFlow.collectLatest {
                    val loading = it.append == LoadState.Loading || it.refresh == LoadState.Loading
                    binding?.progressRvProject?.isVisible = loading && adapter.snapshot().isEmpty()
                    binding?.vEmpty?.isVisible = adapter.snapshot().isEmpty()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCancel()
    }

    class ProjectChooserViewModel(private val apiService: ApiService): BaseViewModel() {

        private val firstPage = 1
        private val itemPerPage = 10

        fun allProject() = Pager(CorePagingSource.config(itemPerPage), pagingSourceFactory = {
            CorePagingSource(firstPage){ page, _ ->
                val data = JSONObject(apiService.allProject(page)).getJSONArray("data").toList<Project>(gson)
                data // List<Product>
            }
        }).flow.cachedIn(viewModelScope)

        fun searchProject(projectName: String) = viewModelScope.launch {
            ApiObserver(
                block = {apiService.searchProject(projectName)},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {

                    }

                }
            )
        }

    }
}