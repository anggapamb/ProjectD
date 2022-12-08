package com.projectd.ui.project.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.crocodic.core.base.adapter.PaginationAdapter
import com.crocodic.core.base.adapter.PaginationLoadState
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.Project
import com.projectd.databinding.FragmentProjectBinding
import com.projectd.databinding.ItemProjectBinding
import com.projectd.databinding.StateLoadingBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectFragment : BaseFragment<FragmentProjectBinding>(R.layout.fragment_project), View.OnClickListener {

    private val viewModel: ProjectViewModel by viewModel()
    private val adapter = PaginationAdapter<ItemProjectBinding, Project>(R.layout.item_project)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddProject?.setOnClickListener(this)

        initView()
        observe()
        viewModel.allProject()
    }

    private fun initView() {
        with(adapter) {
            val loadStateFooter = PaginationLoadState<StateLoadingBinding>(R.layout.state_loading)
            binding?.rvProject?.adapter = withLoadStateFooter(loadStateFooter)
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.allProject().collect {
                        adapter.submitData(it)
                    }
                }
                launch {
                    adapter.loadStateFlow.collectLatest {
                        val loading = it.append == LoadState.Loading || it.refresh == LoadState.Loading
                        binding?.progressRvProject?.isVisible = loading && adapter.snapshot().isEmpty()
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnAddProject -> {
                navigateTo(R.id.actionProjectAddFragment)
            }
        }
    }

}