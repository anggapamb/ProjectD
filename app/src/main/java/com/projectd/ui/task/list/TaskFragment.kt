package com.projectd.ui.task.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.Task
import com.projectd.data.model.TaskDay
import com.projectd.databinding.FragmentTaskBinding
import com.projectd.databinding.ItemDayBinding
import com.projectd.databinding.ItemTaskBinding
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
                    //viewModel.listenTask(it)
                    dayAdapter?.setSelected(position)
                }
            }
        }

        binding?.rvDay?.adapter = dayAdapter
        binding?.rvDay?.scrollToPosition(15)

        binding?.rvTask?.adapter = CoreListAdapter<ItemTaskBinding, Task>(R.layout.item_task).initItem(listTask)
    }

    private fun observe() {
        binding?.vEmpty?.isVisible = listTask.isEmpty()
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
            binding?.btnAddTask -> navigateTo(R.id.actionTaskAddFragment)
        }
    }

}