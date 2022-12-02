package com.projectd.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.textOf
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.bg_login_window)

        binding?.btnLogin?.setOnClickListener(this)

        observe()
        onBackPressedHandle()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                if (!activity?.isFinishing!!) {
                    loadingDialog.show(it.message, it.status == ApiStatus.LOADING)
                    if (it.status == ApiStatus.SUCCESS) {
                        loadingDialog.dismiss()
                        navigateTo(R.id.actionHomeFragment)
                    }
                }
            }
        }
    }

    private fun login() {
        viewModel.login(binding?.etUsername?.textOf(), binding?.etPassword?.textOf())
    }

    private fun onBackPressedHandle() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnLogin -> login()
        }
    }
}