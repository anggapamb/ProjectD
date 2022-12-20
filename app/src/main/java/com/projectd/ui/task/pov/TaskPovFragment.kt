package com.projectd.ui.task.pov

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.tos
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.model.Task
import com.projectd.databinding.FragmentTaskPovBinding
import com.projectd.databinding.ItemUpdateBinding
import com.projectd.ui.dialog.TaskReportDialog
import com.projectd.ui.task.add.TaskAddFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskPovFragment : BaseFragment<FragmentTaskPovBinding>(R.layout.fragment_task_pov) {

    private val viewModel: TaskPovViewModel by viewModel()
    private val listTask = ArrayList<Task?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initView()
        getTasks()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataTasks.collect {
                        binding?.apply {
                            listTask.clear()
                            rvTask.adapter?.notifyDataSetChanged()
                            listTask.addAll(it)
                            rvTask.adapter?.notifyItemInserted(0)
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        val task: Task? = arguments?.getParcelable(Cons.BUNDLE.DATA)
        binding?.task = task

        listTask.let { tasks ->
            binding?.rvTask?.adapter = object : CoreListAdapter<ItemUpdateBinding, Task>(R.layout.item_update) {
                override fun onBindViewHolder(
                    holder: ItemViewHolder<ItemUpdateBinding, Task>,
                    position: Int
                ) {
                    val data = tasks[position]
                    holder.binding.data = data
                    holder.binding.yourName = viewModel.user?.shortName()

                    if (viewModel.user?.devision == Cons.DIVISION.MANAGER || viewModel.user?.devision == Cons.DIVISION.PSDM) {
                        holder.binding.btnMore.isVisible = true
                    } else if (viewModel.user?.isLeader == "true") {
                        holder.binding.btnMore.isVisible = true
                    }

                    holder.itemView.setOnClickListener {
                        if (data?.idLogin == viewModel.user?.id.toString() && data.load != TaskAddFragment.Companion.LOAD.STANDBY) {
                            if ( data.status != Task.DONE) {
                                TaskReportDialog(data) { getTasks() }.show(childFragmentManager, "report")
                            }
                        }
                    }

                    holder.binding.btnMore.setOnClickListener {
                        val moreDialogItems = arrayOf("Verify Task")
                        AlertDialog.Builder(requireContext()).apply {
                            setItems(moreDialogItems) { dialog, which ->
                                dialog.dismiss()
                                when (which) {
                                    0 -> {
                                        if (data?.verified == "0") {
                                            viewModel.verifyTask(data.id.toString(), viewModel.user?.token) { getTasks() }
                                        } else {
                                            requireActivity().tos("Task has been verified by ${data?.verifiedBy}")
                                        }
                                    }
                                }
                            }
                        }.show()
                    }

                }
            }.initItem(tasks)
        }
    }

    private fun getTasks() {
        val task: Task? = arguments?.getParcelable(Cons.BUNDLE.DATA)
        viewModel.taskToday(task?.idLogin)
    }

}