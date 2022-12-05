package com.projectd.ui.task.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.util.Pair
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
import com.projectd.databinding.FragmentTaskAddBinding
import com.projectd.ui.dialog.ProjectChooserDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class TaskAddFragment : BaseFragment<FragmentTaskAddBinding>(R.layout.fragment_task_add), View.OnClickListener {

    private val viewModel: TaskAddViewModel by viewModel()
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var load: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSave?.setOnClickListener(this)

        initView()
        observe()
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

        /* foreclose
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                if (!activity?.isFinishing!!) {
                    loadingDialog.show(it.message, it.status == ApiStatus.LOADING)
                    if (it.status == ApiStatus.SUCCESS) {
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            }
        }
         */
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
                R.id.rb_standby_task -> { load = LOAD.STANDBY }
                R.id.rb_low_task -> { load = LOAD.LOW }
                R.id.rb_medium_task -> { load = LOAD.MEDIUM }
                R.id.rb_high_task -> { load = LOAD.HIGH }
            }
        }
    }

    private fun showProject() {
        ProjectChooserDialog( {
            binding?.etProject?.setText(it?.projectName)
        } , { clearFocus() }, { navigateTo(R.id.actionProjectAddFragment) }).show(childFragmentManager, "project")
    }

    private fun showDatePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .setSelection(Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
            .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            val startDate = Calendar.getInstance().apply {
                timeInMillis = it.first
            }

            val endDate = Calendar.getInstance().apply {
                timeInMillis = it.second
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
        viewModel.addTask(binding?.etTask?.textOf(), binding?.etProject?.textOf(), selectedStartDate, selectedEndDate, load, viewModel.user?.shortName(), viewModel.user?.photo)
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
    }

}