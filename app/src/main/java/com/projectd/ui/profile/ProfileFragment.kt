package com.projectd.ui.profile

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentProfileBinding
import com.projectd.util.AutoStartHelper

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invalidateBatterySetting()

    }

    private fun invalidateBatterySetting() {
        if (AutoStartHelper(requireContext()).isSupport() || AutoStartHelper(requireContext()).autoStartActivity() != null) {
            binding?.alertNotification?.visibility = View.VISIBLE
            binding?.tvBatterySetting?.visibility = View.VISIBLE

            var strSetting = ""
            val strAutoStart = getString(R.string.label_auto_start, Build.MANUFACTURER)
            val strBackgroundService = getString(R.string.label_backgroud_service)

            if (AutoStartHelper(requireContext()).autoStartActivity() != null) {
                strSetting += strAutoStart
            }

            if (AutoStartHelper(requireContext()).isSupport()) {
                if (strSetting.isNotEmpty()) strSetting += " & "
                strSetting += strBackgroundService
            }

            strSetting += "."

            binding?.tvBatterySetting?.movementMethod = LinkMovementMethod.getInstance()
            binding?.tvBatterySetting?.setText(
                getString(
                    R.string.label_white_list,
                    strSetting
                ), TextView.BufferType.SPANNABLE
            )

            val mySpannable = binding?.tvBatterySetting?.text as Spannable

            val autoStartClickable = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    AutoStartHelper(requireContext()).startAutoRunActivity()
                }
            }

            val backgroundServiceClickable = object : ClickableSpan() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onClick(widget: View) {
                    AutoStartHelper(requireContext()).startBatteryOptimizationActivity()
                }
            }

            if (AutoStartHelper(requireContext()).autoStartActivity() != null) {
                mySpannable.setSpan(
                    autoStartClickable,
                    mySpannable.indexOf(strAutoStart),
                    mySpannable.indexOf(strAutoStart) + strAutoStart.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (AutoStartHelper(requireContext()).isSupport()) {
                mySpannable.setSpan(
                    backgroundServiceClickable,
                    mySpannable.indexOf(strBackgroundService),
                    mySpannable.indexOf(strBackgroundService) + strBackgroundService.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            binding?.tvBatterySetting?.highlightColor = Color.TRANSPARENT
            binding?.tvBatterySetting?.setLinkTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue
                )
            )

        } else {
            binding?.alertNotification?.visibility = View.GONE
            binding?.tvBatterySetting?.visibility = View.GONE
        }
    }

}