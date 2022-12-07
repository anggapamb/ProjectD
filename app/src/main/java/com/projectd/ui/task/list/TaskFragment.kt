package com.projectd.ui.task.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.model.Task
import com.projectd.data.model.TaskDay
import com.projectd.databinding.FragmentTaskBinding
import com.projectd.databinding.ItemDayBinding
import com.projectd.databinding.ItemTaskBinding
import com.projectd.ui.dialog.TaskReportDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskFragment : BaseFragment<FragmentTaskBinding>(R.layout.fragment_task), View.OnClickListener {

    private val viewModel: TaskViewModel by viewModel()
    private var dayAdapter: DayTaskAdapter? = null
    private var selectedDate = DateTimeHelper().dateNow()
    private val listTask = ArrayList<Task?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddTask?.setOnClickListener(this)

        initView()
        observe()
    }

    private fun initView() {
        binding?.tvDateNow?.text = DateTimeHelper().datePrettyNow()

        dayAdapter = DayTaskAdapter().apply {
            initItem(ArrayList(viewModel.generate30Days())) { position, data ->
                data?.date?.let {
                    selectedDate = it
                    viewModel.taskByDate(it)
                    dayAdapter?.setSelected(position)
                }
            }
        }

        binding?.rvDay?.adapter = dayAdapter
        binding?.rvDay?.scrollToPosition(15)
        viewModel.taskByDate()

        binding?.rvTask?.adapter = object : CoreListAdapter<ItemTaskBinding, Task>(R.layout.item_task){
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemTaskBinding, Task>,
                position: Int
            ) {
                val data = listTask[position]
                holder.binding.data = data
                holder.binding.btnMore.isVisible = (viewModel.user?.devision == Cons.DIVISION.MANAGER)

                holder.itemView.setOnClickListener {
                    TaskReportDialog(data) { viewModel.taskByDate(selectedDate) }.show(childFragmentManager, "report")
                }

                holder.binding.btnMore.setOnClickListener {
                    val moreDialogItems = arrayOf("Verify Task")
                    AlertDialog.Builder(requireContext()).apply {
                        setItems(moreDialogItems) { dialog, which ->
                            dialog.dismiss()
                            when (which) {
                                0 -> {
                                    if (data?.verified == "0") {
                                        viewModel.verifyTask(data.id.toString(), viewModel.user?.token) { viewModel.taskByDate(selectedDate) }
                                    } else {
                                        requireActivity().tos("Task has been verified by ${data?.verifiedBy}")
                                    }
                                }
                            }
                        }
                    }.show()
                }

            }
        }.initItem(listTask)
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataTasks.collect {
                    binding?.apply {
                        listTask.clear()
                        rvTask.adapter?.notifyDataSetChanged()
                        listTask.addAll(it)
                        rvTask.adapter?.notifyItemInserted(0)
                        vEmpty.isVisible = listTask.isEmpty()
                        progressBar.isVisible = false
                    }
                }
            }
        }
    }

    class DayTaskAdapter : CoreListAdapter<ItemDayBinding, TaskDay>(R.layout.item_day) {
        private var selectedPosition = 15

        fun setSelected(position: Int) {
            items[selectedPosition]?.selected = false
            notifyItemChanged(selectedPosition)

            selectedPosition = position
            items[selectedPosition]?.selected = true
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnAddTask -> {
                if (selectedDate != DateTimeHelper().dateNow()) {
                    Toast.makeText(requireContext(), "Cannot create other today task", Toast.LENGTH_SHORT).show()
                    return
                }
                navigateTo(R.id.actionTaskAddFragment)
            }
        }
    }

}