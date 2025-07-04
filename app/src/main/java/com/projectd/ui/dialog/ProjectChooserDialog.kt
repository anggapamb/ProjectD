package com.projectd.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.crocodic.core.base.adapter.CorePagingSource
import com.crocodic.core.base.adapter.PaginationAdapter
import com.crocodic.core.base.adapter.PaginationLoadState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import com.projectd.databinding.DialogProjectChooserBinding
import com.projectd.databinding.ItemProjectChooserBinding
import com.projectd.databinding.StateLoadingBinding
import com.projectd.paging.ProjectRepository
import com.projectd.paging.ProjectRepositoryImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectChooserDialog(private val onSelect: (Project?) -> Unit, private val onCancel: () -> Unit, private val addProject: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogProjectChooserBinding? = null
    private val viewModel: ProjectChooserViewModel by viewModel()

    private val adapter = object : PaginationAdapter<ItemProjectChooserBinding, Project>(R.layout.item_project_chooser) {
        override fun onBindViewHolder(
            holder: ItemViewHolder<ItemProjectChooserBinding, Project>,
            position: Int
        ) {
            val data = getItem(position)
            holder.binding.data = data
            holder.binding.progressBar.progress = data?.progress?.toInt()!!

            holder.itemView.setOnClickListener {
                onSelect(data)
                this@ProjectChooserDialog.dismiss()
            }
        }
    }

    private var keyword = ""
    private val runnable: () -> Unit = ({
        if (keyword.isEmpty()) {
            lifecycleScope.launch {
                viewModel.projectList().collect {
                    adapter.submitData(it)
                }
            }
        } else {
            lifecycleScope.launch {
                viewModel.searchProject(keyword).collect {
                    adapter.submitData(it)
                }
            }
        }
    })
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_project_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
    }

    private fun initView() {
        with(adapter) {
            val loadStateFooter = PaginationLoadState<StateLoadingBinding>(R.layout.state_loading)
            binding?.rvProject?.adapter = withLoadStateFooter(loadStateFooter)
        }

        binding?.btnAddProject?.setOnClickListener {
            addProject()
        }

        binding?.etSearch?.doAfterTextChanged {
            keyword = it?.toString() ?: ""
            binding?.progressRvProject?.isVisible = true
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 2000)
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            launch {
                viewModel.projectList().collect {
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

    class ProjectChooserViewModel(apiService: ApiService): BaseViewModel() {

        private val repository: ProjectRepository = ProjectRepositoryImpl(apiService, gson)
        private val firstPage = 1
        private val itemPerPage = 10

        fun projectList() = Pager(CorePagingSource.config(itemPerPage), pagingSourceFactory = {
            CorePagingSource(firstPage) { page, limit ->
                repository.getAllProject(page, limit).first() // List<Product>
            }
        }).flow.cachedIn(viewModelScope)


        fun searchProject(projectName: String?) = Pager(CorePagingSource.config(itemPerPage), pagingSourceFactory = {
            CorePagingSource(firstPage) { page, limit ->
                repository.searchProject(projectName, page, limit).first() // List<Product>
            }
        }).flow.cachedIn(viewModelScope)

    }
}