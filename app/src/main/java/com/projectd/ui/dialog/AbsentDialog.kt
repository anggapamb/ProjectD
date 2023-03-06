package com.projectd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.databinding.DialogAbsentBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AbsentDialog(private val onSuccess: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogAbsentBinding? = null
    private val viewModel: AbsentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_absent, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()

        binding?.btnSubmit?.setOnClickListener {
            if (!binding?.etReason?.textOf().isNullOrEmpty()) { activity?.tos("Submitting..", true) }
            viewModel.sendAbsent(viewModel.user?.name, binding?.etReason?.textOf(), viewModel.user?.id.toString())
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                when (it.status) {
                    ApiStatus.SUCCESS -> {
                        onSuccess.invoke()
                        dismiss()
                    }

                    ApiStatus.ERROR -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    class AbsentViewModel(private val apiService: ApiService, private val observe: BaseObserver): BaseViewModel() {

        fun sendAbsent(name: String?, reason: String?, idLogin: String?) = viewModelScope.launch {
            if (name.isNullOrEmpty() || reason.isNullOrEmpty() || idLogin.isNullOrEmpty()) {
                _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
            } else {
                observe(
                    block = {apiService.sendAbsent(reason)},
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