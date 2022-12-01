package com.projectd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Task
import com.projectd.databinding.DialogTaskReportBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskReportDialog(val task: Task?, private val onSuccess: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogTaskReportBinding? = null
    private val viewModel: TaskReportViewModel by viewModel()
    private var status: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_task_report, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.data = task
        binding?.rgStatus?.setOnCheckedChangeListener { _, i ->
            status = when (i) {
                R.id.rb_done -> {
                    binding?.ilReason?.hint = "Congrats! Write some learning by this task..."
                    Task.DONE
                }
                R.id.rb_hold -> {
                    binding?.ilReason?.hint = "Why? Write some notes..."
                    Task.HOLD
                }
                R.id.rb_cancel -> {
                    binding?.ilReason?.hint = "Why? Write some notes..."
                    Task.CANCEL
                }
                else -> null
            }
        }

        observe()
        updateStatus()

    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                binding?.loading = it.status == ApiStatus.LOADING
                if (it.status == ApiStatus.SUCCESS) {
                    onSuccess.invoke()
                    dismiss()
                } else {
                    it.message?.let { msg -> requireContext().tos(msg) }
                }
            }
        }
    }

    private fun updateStatus() {
        binding?.btnSubmit?.setOnClickListener {
            viewModel.updateStatus(task?.id.toString(), status, binding?.etReason?.textOf())
        }
    }

    class TaskReportViewModel(private val apiService: ApiService): BaseViewModel() {

        fun updateStatus(idTask: String?, status: String?, description: String?) = viewModelScope.launch {
            if (idTask.isNullOrEmpty() || status.isNullOrEmpty() || description.isNullOrEmpty()) {
                _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
            } else {
                ApiObserver(
                    block = {apiService.updateTask(idTask, status, description)},
                    toast = false,
                    responseListener = object : ApiObserver.ResponseListener {
                        override suspend fun onSuccess(response: JSONObject) {
                            val message = response.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))
                        }

                        override suspend fun onError(response: ApiResponse) {
                            val message = response.rawResponse?.let { JSONObject(it) }?.getString("message")
                            _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = message))
                        }

                    }
                )
            }
        }

    }

}