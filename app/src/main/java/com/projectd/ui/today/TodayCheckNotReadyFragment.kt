package com.projectd.ui.today

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.User
import com.projectd.data.model.UserNotReady
import com.projectd.databinding.FragmentTodayCheckNotReadyBinding
import com.projectd.databinding.ItemUserNotReadyBinding
import com.projectd.ui.dialog.NoInternetDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TodayCheckNotReadyFragment : BaseFragment<FragmentTodayCheckNotReadyBinding>(R.layout.fragment_today_check_not_ready) {

    private val listUser =  ArrayList<UserNotReady?>()
    private val viewModel: TodayCheckViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initView()
        viewModel.userNotReady()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataUsers.collect {
                        listUser.clear()
                        binding?.rvUser?.adapter?.notifyDataSetChanged()
                        listUser.addAll(it)
                        binding?.rvUser?.adapter?.notifyItemInserted(0)
                        binding?.vEmpty?.isVisible = listUser.isEmpty()
                        binding?.swipeRefresh?.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun initView() {
        binding?.rvUser?.adapter = CoreListAdapter<ItemUserNotReadyBinding, UserNotReady>(R.layout.item_user_not_ready)
            .initItem(listUser)

        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.userNotReady()
            if (!isOnline(context)) {
                NoInternetDialog().show(childFragmentManager, "no_internet")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = TodayCheckNotReadyFragment().apply {
            this.title = title
        }
    }
}