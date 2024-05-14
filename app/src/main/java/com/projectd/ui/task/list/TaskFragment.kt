package com.projectd.ui.task.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
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
import com.projectd.data.Session
import com.projectd.data.model.TaskByDate
import com.projectd.data.model.TaskDay
import com.projectd.databinding.FragmentTaskBinding
import com.projectd.databinding.ItemDayBinding
import com.projectd.databinding.ItemTaskBinding
import com.projectd.ui.dialog.TaskByDateReportDialog
import com.projectd.ui.task.add.TaskAddFragment
import com.projectd.util.getNavigationResultBoolean
import com.projectd.util.setNavigationResult
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskFragment : BaseFragment<FragmentTaskBinding>(R.layout.fragment_task), View.OnClickListener {

    private val viewModel: TaskViewModel by viewModel()
    private val session: Session by inject()
    private var dayAdapter: DayTaskAdapter? = null
    private var selectedDate = DateTimeHelper().dateNow()
    private val listTask = ArrayList<TaskByDate?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddTask?.setOnClickListener(this)

        initView()
        observe()

        setNavigationResult(FROM_TASK, true)
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

        val getResult = getNavigationResultBoolean(TaskAddFragment.Companion.RESULT.key)
        when (getResult?.value ?: true) {
            true -> {
                binding?.rvDay?.scrollToPosition(15)
                viewModel.taskByDate()
            }
            false -> {
                viewModel.taskByDate(tomorrowDate())
                dayAdapter?.setSelected(16)
            }
        }


        binding?.rvTask?.adapter = object : CoreListAdapter<ItemTaskBinding, TaskByDate>(R.layout.item_task){
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemTaskBinding, TaskByDate>,
                position: Int
            ) {
                val data = listTask[position]
                holder.binding.data = data

                when (session.getUser()?.devision?.id) {
                    Cons.DIVISION.MANAGER -> {
                        holder.binding.btnMore.isVisible = data?.projectDetail?.idProjectDirector == session.getUser()?.id
                    }
                    Cons.DIVISION.PSDM -> {
                        holder.binding.btnMore.isVisible = data?.load != TaskAddFragment.Companion.LOAD.STANDBY
                    }
                    else -> holder.binding.btnMore.isVisible = session.getUser()?.isLeader == true
                }

                holder.itemView.setOnClickListener {
                    if (data?.status != TaskByDate.DONE && data?.load != TaskAddFragment.Companion.LOAD.STANDBY) {
                        TaskByDateReportDialog(data) { viewModel.taskByDate(selectedDate) }.show(childFragmentManager, "report")
                    }
                }

                holder.binding.btnMore.setOnClickListener {
                    val moreDialogItems = arrayOf("Verify Task")
                    AlertDialog.Builder(requireContext()).apply {
                        setItems(moreDialogItems) { dialog, which ->
                            dialog.dismiss()
                            when (which) {
                                0 -> {
                                    if (data?.verified == false) {
                                        viewModel.verifyTask(data.id.toString()) { viewModel.taskByDate(selectedDate) }
                                    } else {
                                        requireActivity().tos("Task has been verified by ${data?.verifiedBy?.name}")
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

    private fun tomorrowDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, +1)
        return dateFormat.format(cal.time)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnAddTask -> {
                val bundle = bundleOf(Cons.BUNDLE.DATA to selectedDate)
                when (selectedDate) {
                    DateTimeHelper().dateNow() -> {
                        navigateTo(R.id.actionTaskAddFragment, bundle)
                    }
                    tomorrowDate() -> {
                        navigateTo(R.id.actionTaskAddFragment, bundle)
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Task date must be today or tomorrow", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val FROM_TASK = "from_task"
    }

}