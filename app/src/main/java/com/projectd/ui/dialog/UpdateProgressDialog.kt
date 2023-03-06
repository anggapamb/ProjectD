package com.projectd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Project
import com.projectd.databinding.DialogUpdateProgressBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateProgressDialog(val project: Project, private val onDismiss: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogUpdateProgressBinding? = null
    private val viewModel: UpdateProgressViewModel by viewModel()
    private var progress: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_update_progress, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        project.progress?.let { binding?.seekBar?.progress = it }

        binding?.seekBar?.min = 0
        binding?.seekBar?.max = 100

        binding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progress = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) { }

            override fun onStopTrackingTouch(p0: SeekBar?) { }

        })

        binding?.btnUpdate?.setOnClickListener {
            viewModel.updateProgress(project, progress)
        }

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                when (it.status) {
                    ApiStatus.SUCCESS -> {
                        onDismiss.invoke()
                        dismiss()
                    }
                    ApiStatus.ERROR -> {
                        onDismiss.invoke()
                        dismiss()
                    }
                    else -> {}
                }
            }
        }
    }

    class UpdateProgressViewModel(private val apiService: ApiService, private val observer: BaseObserver): BaseViewModel() {

        fun updateProgress(project: Project, progress: Int) = viewModelScope.launch{
            observer(
                block = {apiService.updateProgressProject(project.id, progress)},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                       _apiResponse.send(ApiResponse(ApiStatus.SUCCESS))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.send(ApiResponse(ApiStatus.ERROR))
                    }
                }
            )
        }

    }
}