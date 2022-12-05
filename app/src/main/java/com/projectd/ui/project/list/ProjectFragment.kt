package com.projectd.ui.project.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.Project
import com.projectd.databinding.FragmentProjectBinding
import com.projectd.databinding.ItemProjectBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectFragment : BaseFragment<FragmentProjectBinding>(R.layout.fragment_project), View.OnClickListener {

    private val viewModel: ProjectViewModel by viewModel()
    private val listProject = ArrayList<Project?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddProject?.setOnClickListener(this)

        initView()
        observe()
        viewModel.allProject()
    }

    private fun initView() {
        binding?.rvProject?.adapter = object : CoreListAdapter<ItemProjectBinding, Project>(R.layout.item_project) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemProjectBinding, Project>,
                position: Int
            ) {
                val data = listProject[position]
                holder.binding.data = data
            }
        }.initItem(listProject)
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataProjects.collect {
                        listProject.clear()
                        binding?.rvProject?.adapter?.notifyDataSetChanged()
                        listProject.addAll(it)
                        binding?.rvProject?.adapter?.notifyItemInserted(0)
                        binding?.vEmpty?.isVisible = listProject.isEmpty()
                        binding?.progressRvProject?.isVisible = false
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