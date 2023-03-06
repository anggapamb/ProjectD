package com.projectd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.toList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Task
import com.projectd.databinding.DialogTaskChooserBinding
import com.projectd.databinding.ItemTaskChooserBinding
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TaskChooserDialog(private val onSelect: (Task?) -> Unit): BottomSheetDialogFragment() {

    private var binding: DialogTaskChooserBinding? = null
    private val viewModel: TaskChooserViewModel by viewModel()
    private val listTask = ArrayList<Task?>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_task_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initView()
        viewModel.taskByDate(yesterdayDate())
    }

    private fun initView() {
        binding?.rvTask?.adapter = CoreListAdapter<ItemTaskChooserBinding, Task>(R.layout.item_task_chooser)
            .initItem(listTask) { _, data ->
                onSelect(data)
                this@TaskChooserDialog.dismiss()
            }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.dataTasks.collect {
                binding?.apply {
                    listTask.clear()
                    rvTask.adapter?.notifyDataSetChanged()
                    listTask.addAll(it)
                    rvTask.adapter?.notifyItemInserted(0)
                    vEmpty.isVisible = listTask.isEmpty()
                    progressBar.isVisible = false
                }
            }
        }
    }

    private fun yesterdayDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }

    class TaskChooserViewModel(private val apiService: ApiService, private val observer: BaseObserver): BaseViewModel() {

        private val _dataTasks: Channel<List<Task>> = Channel()
        val dataTasks = _dataTasks.receiveAsFlow()

        fun taskByDate(date: String) = viewModelScope.launch {
            observer(
                block = {apiService.taskByDate(date)},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONArray("data").toList<Task>(gson)
                        _dataTasks.send(customData(data))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        _dataTasks.send(emptyList())
                    }

                }
            )
        }

        private fun customData(data: List<Task>): List<Task> {
            val tasks = ArrayList<Task>()
            data.forEach { if (it.status != Task.DONE) { tasks.add(it) } }
            return tasks
        }
    }
}