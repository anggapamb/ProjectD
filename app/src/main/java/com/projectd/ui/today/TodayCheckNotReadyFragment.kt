package com.projectd.ui.today

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentTodayCheckNotReadyBinding

class TodayCheckNotReadyFragment : BaseFragment<FragmentTodayCheckNotReadyBinding>(R.layout.fragment_today_check_not_ready) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today_check_not_ready, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = TodayCheckNotReadyFragment().apply {
            this.title = title
        }
    }
}