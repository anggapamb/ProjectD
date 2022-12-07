package com.projectd.ui.today

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.tos
import com.projectd.R
import com.projectd.api.ApiService
import com.projectd.base.fragment.BaseFragment
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.Absent
import com.projectd.databinding.FragmentTodayAbsentBinding
import com.projectd.databinding.ItemAbsentBinding
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayAbsentFragment : BaseFragment<FragmentTodayAbsentBinding>(R.layout.fragment_today_absent) {

    private val viewModel: TodayAbsentViewModel by viewModel()
    private val listAbsent = ArrayList<Absent?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observe()
        viewModel.listAllAbsent()
    }

    private fun initView() {
        binding?.rvUpdate?.adapter = CoreListAdapter<ItemAbsentBinding, Absent>(R.layout.item_absent)
            .initItem(listAbsent) { _, data ->
                val moreDialogItems = arrayOf("Approve", "Reject")
                AlertDialog.Builder(requireContext()).apply {
                    setItems(moreDialogItems) { dialog, which ->
                        dialog.dismiss()
                        when (which) {
                            0 -> {
                                if (data?.approved == null) {
                                    viewModel.approvedAbsent("${data?.id}", "true") { viewModel.listAllAbsent() }
                                } else {
                                    val aprvl = if (data.approved == "true") "approved" else "rejected"
                                    requireActivity().tos("Absent already $aprvl by ${data.approvedBy}")
                                }
                            }
                            1 -> {
                                if (data?.approved == null) {
                                    viewModel.approvedAbsent("${data?.id}", "false") { viewModel.listAllAbsent() }
                                } else {
                                    val aprvl = if (data.approved == "true") "approved" else "rejected"
                                    requireActivity().tos("Absent already $aprvl by ${data.approvedBy}")
                                }
                            }
                        }
                    }
                }.show()
            }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataAbsents.collect {
                        listAbsent.clear()
                        binding?.rvUpdate?.adapter?.notifyDataSetChanged()
                        listAbsent.addAll(it)
                        binding?.rvUpdate?.adapter?.notifyItemInserted(0)
                        binding?.vEmpty?.isVisible = listAbsent.isEmpty()
                        binding?.progressBar?.isVisible = false
                    }
                }
            }
        }
    }

    class TodayAbsentViewModel(private val apiService: ApiService): BaseViewModel() {

        private val _dataAbsents: Channel<List<Absent?>> = Channel()
        val dataAbsents = _dataAbsents.receiveAsFlow()

        fun listAllAbsent() = viewModelScope.launch {
            ApiObserver(
                block = {apiService.getListAllAbsent()},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val data = response.getJSONArray("data").toList<Absent>(gson)
                        _dataAbsents.send(data)
                    }

                    override suspend fun onError(response: ApiResponse) {
                        _dataAbsents.send(emptyList())
                    }

                }
            )
        }

        fun approvedAbsent(idAbsent: String, approved: String, onResponse: () -> Unit) = viewModelScope.launch {
            ApiObserver(
                block = {apiService.approvedAbsent(idAbsent, approved)},
                toast = false,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        onResponse.invoke()
                    }

                }
            )
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = TodayAbsentFragment().apply {
            this.title = title
        }
    }
}