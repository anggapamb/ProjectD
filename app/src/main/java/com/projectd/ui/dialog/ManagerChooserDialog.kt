package com.projectd.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.toList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Manager
import com.projectd.databinding.DialogManagerChooserBinding
import com.projectd.databinding.ItemManagerBinding
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManagerChooserDialog(private val title :String, private val onSelect: (Manager?) -> Unit, private val onCancel: () -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogManagerChooserBinding? = null
    private val viewModel: ManagerChooserViewModel by viewModel()
    private val listManager = ArrayList<Manager?>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_manager_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
        viewModel.managers()
    }

    private fun initView() {
        binding?.title = title
        binding?.rvManager?.adapter = CoreListAdapter<ItemManagerBinding, Manager>(R.layout.item_manager)
            .initItem(listManager) { _, data ->
                onSelect(data)
                this@ManagerChooserDialog.dismiss()
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataManagers.collect {
                        listManager.clear()
                        binding?.rvManager?.adapter?.notifyDataSetChanged()
                        listManager.addAll(it)
                        binding?.rvManager?.adapter?.notifyItemInserted(0)
                        binding?.vEmpty?.isVisible = listManager.isEmpty()
                        binding?.progressRvManager?.isVisible = false
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCancel()
    }

    class ManagerChooserViewModel(private val apiService: ApiService) : BaseViewModel() {

        private val _dataManagers: Channel<List<Manager?>> = Channel()
        val dataManagers = _dataManagers.receiveAsFlow()

        fun managers() = viewModelScope.launch {
            ApiObserver(
                block = {apiService.managers()},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONArray("data").toList<Manager>(gson)
                        _dataManagers.send(data)
                    }

                    override suspend fun onError(response: ApiResponse) {
                        _dataManagers.send(emptyList())
                    }

                }
            )
        }

    }

}