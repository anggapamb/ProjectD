package com.projectd.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.toList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import com.projectd.databinding.DialogProjectChooserBinding
import com.projectd.databinding.ItemProjectChooserBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProjectChooserDialog(private val onSelect: (Project?) -> Unit, private val onCancel: () -> Unit, private val addProject: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogProjectChooserBinding? = null
    private val viewModel: ProjectChooserViewModel by viewModel()
    private val listProject = ArrayList<Project?>()
    private val allListProject = ArrayList<Project?>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_project_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
        viewModel.allProject()
    }

    private fun initView() {
        binding?.rvProject?.adapter = CoreListAdapter<ItemProjectChooserBinding, Project>(R.layout.item_project_chooser)
            .initItem(listProject) { _, data ->
                onSelect(data)
                this@ProjectChooserDialog.dismiss()
            }

        binding?.btnAddProject?.setOnClickListener {
            addProject()
        }

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (binding?.etSearch?.text?.length == 0) {
                    listProject.clear()
                    binding?.rvProject?.adapter?.notifyDataSetChanged()
                    listProject.addAll(allListProject)
                    binding?.rvProject?.adapter?.notifyItemInserted(0)
                    binding?.vEmpty?.isVisible = listProject.isEmpty()
                }
                else {
                    val search = allListProject.filter { it?.projectName?.contains(p0.toString(), true) == true }
                    listProject.clear()
                    binding?.rvProject?.adapter?.notifyDataSetChanged()
                    listProject.addAll(search)
                    binding?.rvProject?.adapter?.notifyItemInserted(0)
                    binding?.vEmpty?.isVisible = listProject.isEmpty()
                }
            }

        })
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.dataProjects.observe(viewLifecycleOwner) {
                listProject.clear()
                allListProject.clear()
                binding?.rvProject?.adapter?.notifyDataSetChanged()
                listProject.addAll(it)
                allListProject.addAll(it)
                binding?.rvProject?.adapter?.notifyItemInserted(0)
                binding?.vEmpty?.isVisible = listProject.isEmpty()
                binding?.progressRvProject?.isVisible = false
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCancel()
    }

    class ProjectChooserViewModel(private val apiService: ApiService): BaseViewModel() {

        val dataProjects = MutableLiveData<List<Project?>>()

        fun allProject() = viewModelScope.launch {
            ApiObserver(
                block = {apiService.allProject()},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONArray("data").toList<Project>(gson)
                        dataProjects.postValue(data)
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        dataProjects.postValue(emptyList())
                    }

                }
            )
        }

    }
}