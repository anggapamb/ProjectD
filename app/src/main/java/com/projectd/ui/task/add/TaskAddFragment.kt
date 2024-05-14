package com.projectd.ui.task.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.textOf
import com.crocodic.core.helper.DateTimeHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Task
import com.projectd.databinding.FragmentTaskAddBinding
import com.projectd.ui.dialog.ProjectChooserDialog
import com.projectd.ui.dialog.TaskChooserDialog
import com.projectd.util.setNavigationResult
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TaskAddFragment : BaseFragment<FragmentTaskAddBinding>(R.layout.fragment_task_add), View.OnClickListener {

    private val viewModel: TaskAddViewModel by viewModel()
    private val session: Session by inject()
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var load: String? = null
    private var timelineIsFilled = false
    private var idProject: Int? = null
    private var selectedTaskDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSave?.setOnClickListener(this)

        selectedTaskDate = arguments?.getString(Cons.BUNDLE.DATA)

        initView()
        observe()

        val bool = selectedTaskDate == DateTimeHelper().dateNow()
        setNavigationResult(RESULT.key, bool)
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        loadingDialog.show(it.message, it.status == ApiStatus.LOADING)
                        if (it.status == ApiStatus.SUCCESS) {
                            loadingDialog.dismiss()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        binding?.etProject?.setOnFocusChangeListener { _, b ->
            if (b) showProject()
        }

        binding?.etStartDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker()
        }

        binding?.etEndDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker()
        }

        binding?.rgDiff?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_standby_task -> {
                    load = LOAD.STANDBY
                    binding?.apply {
                        ilProject.isVisible = false
                        vTimeline.isVisible = false
                    }
                }
                R.id.rb_low_task -> {
                    load = LOAD.LOW
                    binding?.apply {
                        ilProject.isVisible = true
                        vTimeline.isVisible = !timelineIsFilled
                    }
                }
                R.id.rb_medium_task -> {
                    load = LOAD.MEDIUM
                    binding?.apply {
                        ilProject.isVisible = true
                        vTimeline.isVisible = !timelineIsFilled
                    }
                }
                R.id.rb_high_task -> {
                    load = LOAD.HIGH
                    binding?.apply {
                        ilProject.isVisible = true
                        vTimeline.isVisible = !timelineIsFilled
                    }
                }
            }
        }

        binding?.btnCopyTask?.setOnClickListener {
            TaskChooserDialog {

                binding?.etTask?.setText(it?.taskName)
                binding?.etProject?.setText(it?.projectDetail?.projectName)

                when (it?.load) {
                    Task.STANDBY -> {
                        load = LOAD.STANDBY
                        binding?.apply {
                            rbStandbyTask.performClick()
                            ilProject.isVisible = false
                            vTimeline.isVisible = false
                        }

                    }
                    Task.LOW -> {
                        load = LOAD.LOW
                        binding?.apply {
                            rbLowTask.performClick()
                            ilProject.isVisible = true
                            vTimeline.isVisible = true
                        }
                    }
                    Task.MEDIUM -> {
                        load = LOAD.MEDIUM
                        binding?.apply {
                            rbMediumTask.performClick()
                            ilProject.isVisible = true
                            vTimeline.isVisible = true
                        }
                    }
                    Task.HIGH -> {
                        load = LOAD.HIGH
                        binding?.apply {
                            rbHighTask.performClick()
                            ilProject.isVisible = true
                            vTimeline.isVisible = true
                        }
                    }
                }

            }.show(childFragmentManager, "task")
        }
    }

    private fun showProject() {
        ProjectChooserDialog( { project ->
            binding?.etProject?.setText(project?.projectName)

            binding?.vTimeline?.isVisible = true
            idProject = project?.id
            timelineIsFilled = false

            project?.timelines?.forEach { timeLine ->
                if (timeLine?.devisionId == session.getUser()?.devision?.id) {
                    binding?.vTimeline?.isVisible = false
                    timelineIsFilled = true
                    return@forEach
                }
            }

            /*
            when (viewModel.user?.devision) {
                Cons.DIVISION.MOBILE -> {
                    if (!it?.timeline?.mobile?.startDate.isNullOrEmpty() || !it?.timeline?.mobile?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true

                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.WEB -> {
                    if (!it?.timeline?.web?.startDate.isNullOrEmpty() || !it?.timeline?.web?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.TESTER -> {
                    if (!it?.timeline?.tester?.startDate.isNullOrEmpty() || !it?.timeline?.tester?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.ANALYST -> {
                    if (!it?.timeline?.analyst?.startDate.isNullOrEmpty() || !it?.timeline?.analyst?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.MARKETING -> {
                    if (!it?.timeline?.marketing?.startDate.isNullOrEmpty() || !it?.timeline?.marketing?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.PSDM -> {
                    if (!it?.timeline?.psdm?.startDate.isNullOrEmpty() || !it?.timeline?.psdm?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.SUPER_ADMIN -> {
                    if (!it?.timeline?.super_admin?.startDate.isNullOrEmpty() || !it?.timeline?.super_admin?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
                Cons.DIVISION.MANAGER -> {
                    if (!it?.timeline?.manager?.startDate.isNullOrEmpty() || !it?.timeline?.manager?.endDate.isNullOrEmpty()) {
                        binding?.vTimeline?.isVisible = false
                        timelineIsFilled = true
                    } else {
                        binding?.vTimeline?.isVisible = true
                        timelineIsFilled = false
                    }
                }
            }
            */

        } , { clearFocus() }, { navigateTo(R.id.actionProjectAddFragment) }).show(childFragmentManager, "project")
    }

    private fun showDatePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
            .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            val startDate = Calendar.getInstance().apply {
                if (it.first != null) {
                    timeInMillis = it.first!!
                }
            }

            val endDate = Calendar.getInstance().apply {
                if (it.second != null) {
                    timeInMillis = it.second!!
                }
            }

            selectedStartDate = DateTimeHelper().fromDate(startDate.time)
            selectedEndDate = DateTimeHelper().fromDate(endDate.time)

            binding?.etStartDate?.setText(DateTimeHelper().convert(selectedStartDate, "yyyy-MM-dd", "d MMM yyy"))
            binding?.etEndDate?.setText(DateTimeHelper().convert(selectedEndDate, "yyyy-MM-dd", "d MMM yyy"))
        }

        dateRangePicker.addOnDismissListener {
            clearFocus()
        }

        dateRangePicker.show(childFragmentManager, "date")
    }

    private fun clearFocus() {
        binding?.etProject?.clearFocus()
        binding?.etStartDate?.clearFocus()
        binding?.etEndDate?.clearFocus()
    }

    private fun addTask() {
        viewModel.addTask(binding?.etTask?.textOf(), idProject.toString(),
            selectedStartDate, selectedEndDate, load, session.getUser()?.shortName(), session.getUser()?.photo, timelineIsFilled, "$selectedTaskDate")
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnSave -> addTask()
        }
    }

    companion object {
        object LOAD {
            const val STANDBY = "standby"
            const val LOW = "low"
            const val MEDIUM = "medium"
            const val HIGH = "high"
        }

        object RESULT {
            const val key = "task_add"
        }
    }

}