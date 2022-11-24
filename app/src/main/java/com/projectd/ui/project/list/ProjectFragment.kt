package com.projectd.ui.project.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.crocodic.core.base.adapter.CoreListAdapter
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.Project
import com.projectd.databinding.FragmentProjectBinding
import com.projectd.databinding.ItemProjectBinding

class ProjectFragment : BaseFragment<FragmentProjectBinding>(R.layout.fragment_project), View.OnClickListener {

    private val viewModel: ProjectViewModel by viewModels()
    private val listProject = ArrayList<Project?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddProject?.setOnClickListener(this)

        initView()
        observe()
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
        binding?.vEmpty?.isVisible = listProject.isEmpty()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnAddProject -> {
                navigateTo(R.id.actionProjectAddFragment)
            }
        }
    }

}