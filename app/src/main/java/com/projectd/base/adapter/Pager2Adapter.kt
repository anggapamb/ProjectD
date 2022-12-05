package com.projectd.base.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projectd.base.fragment.BaseFragment

class Pager2Adapter(fragment: Fragment, private val items: Array<BaseFragment<*>>): FragmentStateAdapter(fragment) {
    override fun getItemCount() = items.size
    override fun createFragment(position: Int) = items[position]
}