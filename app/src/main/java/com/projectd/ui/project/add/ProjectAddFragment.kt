package com.projectd.ui.project.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import com.crocodic.core.helper.DateTimeHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentProjectAddBinding
import java.util.*

class ProjectAddFragment : BaseFragment<FragmentProjectAddBinding>(R.layout.fragment_project_add), View.OnClickListener {

    private val viewModel: ProjectAddViewModel by viewModels()
    private var difficult = MEDIUM
    var selectedStartDate: String? = null
    var selectedEndDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSave?.setOnClickListener(this)

        initView()
        setEnabledButton()
    }

    private fun initView() {
        binding?.etStartDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker(true)
        }

        binding?.etEndDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker(false)
        }

        // TODO: show manager dialog
    }

    private fun addProject() {

    }

    private fun showDatePicker(start: Boolean) {
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
        binding?.etStartDate?.clearFocus()
        binding?.etEndDate?.clearFocus()
        binding?.etPd?.clearFocus()
    }

    private fun setEnabledButton() {
        binding?.etProjectName?.addTextChangedListener(watcher)
        binding?.etDescription?.addTextChangedListener(watcher)
        binding?.etStartDate?.addTextChangedListener(watcher)
        binding?.etEndDate?.addTextChangedListener(watcher)
        binding?.etPd?.addTextChangedListener(watcher)
        binding?.rgDiff?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_medium_add_project -> difficult = MEDIUM
                R.id.rb_high_add_project -> difficult = HARD
            }
        }
    }

    private val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding?.btnSave?.isEnabled =
                !(binding?.etProjectName?.text?.length == 0 || binding?.etDescription?.text?.length == 0 || binding?.etStartDate?.text?.length == 0 ||
                        binding?.etEndDate?.text?.length == 0 || binding?.etPd?.text?.length == 0)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnSave -> addProject()
        }
    }

    companion object {
        const val MEDIUM = "medium"
        const val HARD = "hard"
    }
}