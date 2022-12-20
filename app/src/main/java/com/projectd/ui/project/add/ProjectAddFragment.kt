package com.projectd.ui.project.add

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
import com.projectd.data.Cons
import com.projectd.data.model.Project
import com.projectd.databinding.FragmentProjectAddBinding
import com.projectd.ui.dialog.ManagerChooserDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ProjectAddFragment : BaseFragment<FragmentProjectAddBinding>(R.layout.fragment_project_add), View.OnClickListener {

    private val viewModel: ProjectAddViewModel by viewModel()

    private var difficult: String? = null
    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null
    private var pdShortName: String? = ""

    var project: Project? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSave?.setOnClickListener(this)

        initView()
        observe()
    }

    private fun initView() {

        // TODO: deprecated parcelable
        project = arguments?.getParcelable(Cons.BUNDLE.DATA)
        binding?.project = project

        if (project != null) {
            selectedStartDate = project?.startDate
            selectedEndDate = project?.endDate
            pdShortName = project?.projectDirector
        }

        binding?.etStartDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker()
        }

        binding?.etEndDate?.setOnFocusChangeListener { _, b ->
            if (b) showDatePicker()
        }

        binding?.etPd?.setOnFocusChangeListener { _, b ->
            if (b) showManager()
        }

        binding?.rgDiff?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_medium_add_project -> difficult = MEDIUM
                R.id.rb_high_add_project -> difficult = HARD
            }
        }
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

    private fun showManager() {
        ManagerChooserDialog(manager, {
            pdShortName = it?.shortName()
            binding?.etPd?.setText(it?.name)
        }) { clearFocus() }.show(childFragmentManager, "manager")
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
        binding?.etStartDate?.clearFocus()
        binding?.etEndDate?.clearFocus()
        binding?.etPd?.clearFocus()
    }

    private fun addProject() {
        if (project == null) {
            viewModel.addProject(null, binding?.etProjectName?.textOf(), binding?.etDescription?.textOf(), selectedStartDate, selectedEndDate,
                pdShortName, difficult, viewModel.user?.shortName(), null)
        } else {
            viewModel.addProject(project?.id.toString(),binding?.etProjectName?.textOf(), binding?.etDescription?.textOf(), selectedStartDate, selectedEndDate,
                pdShortName, difficult, viewModel.user?.shortName(), project?.progress)
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

        const val manager = "Manager"
    }
}