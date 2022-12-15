package com.projectd.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.textOf
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login), View.OnClickListener {

    private val viewModel: LoginViewModel by viewModel()
    private val session: CoreSession by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.bg_login_window)

        binding?.btnLogin?.setOnClickListener(this)

        initFcmToken()
        observe()
    }

    private fun initFcmToken() {
        viewModel.saveFirebaseRegId {
            session.setValue(Cons.DB.USER.FCM_ID, it)
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        loadingDialog.show(it.message, it.status == ApiStatus.LOADING)
                        if (it.status == ApiStatus.SUCCESS) {
                            loadingDialog.dismiss()
                            navigateTo(R.id.actionHomeFragment)
                        }
                    }
                }
            }
        }
    }

    private fun login() {
        viewModel.login(binding?.etUsername?.textOf(), binding?.etPassword?.textOf())
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding?.btnLogin -> login()
        }
    }
}