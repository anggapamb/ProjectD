package com.projectd.ui.today

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.tos
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.Absent
import com.projectd.databinding.FragmentTodayAbsentBinding
import com.projectd.databinding.ItemAbsentBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayAbsentFragment : BaseFragment<FragmentTodayAbsentBinding>(R.layout.fragment_today_absent) {

    private val viewModel: TodayCheckViewModel by viewModel()
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

        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.listAllAbsent()
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataAbsents.collect {
                        binding?.apply {
                            listAbsent.clear()
                            rvUpdate.adapter?.notifyDataSetChanged()
                            listAbsent.addAll(it)
                            rvUpdate.adapter?.notifyItemInserted(0)
                            vEmpty.isVisible = listAbsent.isEmpty()
                            progressBar.isVisible = false
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = TodayAbsentFragment().apply {
            this.title = title
        }
    }
}