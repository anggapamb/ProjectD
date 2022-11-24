package com.projectd.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snack
import com.crocodic.core.extension.textOf
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.databinding.FragmentLoginBinding
import com.projectd.util.isValidEmail
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.bg_login_window)

        binding?.btnLogin?.setOnClickListener(this)

        observe()
        setEnabledButton()
        //onBackPressedHandle()
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.apiResponse.collect {
                when (it.status) {
                    ApiStatus.SUCCESS -> {
                        loadingDialog.dismiss()
                        navigateTo(R.id.actionHomeFragment)
                    }
                    ApiStatus.ERROR -> {
                        loadingDialog.dismiss()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun login() {
        if (binding?.etUsername?.textOf()?.let { isValidEmail(it) } == true) {
            loadingDialog.show("Wait..", true)
            viewModel.login(binding?.etUsername?.textOf(), binding?.etPassword?.textOf())
        } else {
            binding?.root?.snack("Invalid email")
        }
    }



    private fun setEnabledButton() {
        binding?.etUsername?.addTextChangedListener(watcher)
        binding?.etPassword?.addTextChangedListener(watcher)
    }

    private val watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            binding?.btnLogin?.isEnabled = !(binding?.etUsername?.text?.length == 0 || binding?.etPassword?.text?.length == 0)
        }
    }

    private fun onBackPressedHandle() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finishAffinity()
                }
            })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnLogin -> login()
        }
    }
}