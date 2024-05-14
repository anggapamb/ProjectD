package com.projectd.ui.project.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.crocodic.core.base.adapter.PaginationAdapter
import com.crocodic.core.base.adapter.PaginationLoadState
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Project
import com.projectd.databinding.FragmentProjectBinding
import com.projectd.databinding.ItemProjectBinding
import com.projectd.databinding.StateLoadingBinding
import com.projectd.ui.dialog.NoInternetDialog
import com.projectd.ui.dialog.UpdateProgressDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectFragment : BaseFragment<FragmentProjectBinding>(R.layout.fragment_project), View.OnClickListener {

    private val viewModel: ProjectViewModel by viewModel()
    private val session: Session by inject()
    private val adapter = object : PaginationAdapter<ItemProjectBinding, Project>(R.layout.item_project) {
        override fun onBindViewHolder(
            holder: ItemViewHolder<ItemProjectBinding, Project>,
            position: Int
        ) {
            val item = getItem(position)
            holder.binding.data = item
            holder.binding.btnMore.isVisible = (item?.projectDirector?.id == session.getUser()?.id || item?.createdBy?.id == session.getUser()?.id)

            holder.binding.progressBar.progress = item?.progress?.toInt()!!

            holder.binding.btnMore.setOnClickListener {
                val moreDialogItems = arrayListOf("Update Progress", "Edit")
                AlertDialog.Builder(requireContext()).apply {
                    setItems(moreDialogItems.toTypedArray()) { dialog, which ->
                        dialog.dismiss()
                        when (which) {
                            0 -> {
                                UpdateProgressDialog(item) { observe() }.show(childFragmentManager, "update_progress")
                            }
                            1 -> {
                                val bundle = bundleOf(Cons.BUNDLE.DATA to item)
                                navigateTo(R.id.actionProjectAddFragment, bundle)
                            }
                        }
                    }
                }.show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddProject?.setOnClickListener(this)

        initView()
        observe()
    }

    private fun initView() {
        with(adapter) {
            val loadStateFooter = PaginationLoadState<StateLoadingBinding>(R.layout.state_loading)
            binding?.rvProject?.adapter = withLoadStateFooter(loadStateFooter)
        }

        binding?.swipeRefresh?.setOnRefreshListener {
            observe()
            if (!isOnline(requireContext())) {
                NoInternetDialog().show(childFragmentManager, "no_internet")
                binding?.swipeRefresh?.isRefreshing = false
            }
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                        binding?.swipeRefresh?.isRefreshing = loading && adapter.snapshot().isEmpty()
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