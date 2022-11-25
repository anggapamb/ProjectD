package com.projectd.ui.task.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
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
    private var load = LOAD.LOW

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSave?.setOnClickListener(this)

        initView()
        observe()
        setEnabledButton()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                when (it.status) {
                    ApiStatus.SUCCESS -> {
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }

                    ApiStatus.ERROR -> {
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initView() {
        binding?.etProject?.setOnFocusChangeListener { view, b ->
            if (b) showProject()
        }

        binding?.etStartDate?.setOnFocusChangeListener { view, b ->
            if (b) showDatePicker()
        }

        binding?.etEndDate?.setOnFocusChangeListener { view, b ->
            if (b) showDatePicker()
        }
    }

    private fun setEnabledButton() {
        binding?.etTask?.addTextChangedListener(watcher)
        binding?.etProject?.addTextChangedListener(watcher)
        binding?.etStartDate?.addTextChangedListener(watcher)
        binding?.etEndDate?.addTextChangedListener(watcher)
        binding?.rgDiff?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_standby_task -> load = LOAD.STANDBY
                R.id.rb_low_task -> load = LOAD.LOW
                R.id.rb_medium_task -> load = LOAD.MEDIUM
                R.id.rb_high_task -> load = LOAD.HIGH
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

    private val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding?.btnSave?.isEnabled = !(binding?.etProject?.text?.length == 0 || binding?.etTask?.text?.length == 0
                    || binding?.etStartDate?.text?.length == 0 || binding?.etEndDate?.text?.length == 0)
        }
    }

    private fun addTask() {
        loadingDialog.show("Wait", true)
        viewModel.addTask(binding?.etTask?.textOf(), binding?.etProject?.textOf(), selectedStartDate, selectedEndDate, load, viewModel.user?.shortName(), viewModel.user?.photo!!)
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