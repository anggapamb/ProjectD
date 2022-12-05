package com.projectd.ui.today

import android.os.Bundle
import android.view.View
import com.crocodic.core.helper.DateTimeHelper
import com.google.android.material.tabs.TabLayoutMediator
import com.projectd.R
import com.projectd.base.adapter.Pager2Adapter
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentTodayCheckBinding

class TodayCheckFragment : BaseFragment<FragmentTodayCheckBinding>(R.layout.fragment_today_check) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding?.tvDateNow?.text = DateTimeHelper().datePrettyNow()

        val pages = arrayOf(
            TodayCheckNotReadyFragment.newInstance("Not Ready") as BaseFragment<*>,
            TodayAbsentFragment.newInstance("Absent") as BaseFragment<*>,
            TodayCheckListFragment.newInstance("Standby") as BaseFragment<*>,
            TodayCheckListFragment.newInstance("On-going") as BaseFragment<*>,
            TodayCheckListFragment.newInstance("Done") as BaseFragment<*>,
            TodayCheckListFragment.newInstance("Cancel") as BaseFragment<*>
        )

        binding?.viewPager?.adapter = Pager2Adapter(this, pages)
        binding?.viewPager?.offscreenPageLimit = 6

        TabLayoutMediator(binding?.tabLayout ?: return, binding?.viewPager ?: return, false, false) { tab, position ->
            tab.text = pages[position].title
        }.attach()
    }

}