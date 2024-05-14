package com.projectd.base.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.projectd.base.App
import com.projectd.ui.dialog.LoadingDialog
import com.projectd.ui.dialog.NoInternetDialog
import com.projectd.ui.home.HomeActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

open class BaseFragment<VB : ViewDataBinding>(@LayoutRes private val layoutRes: Int): Fragment() {

    open var title: String = ""
    open var hasLoadedOnce = false

    protected var binding: VB? = null

    protected val loadingDialog: LoadingDialog by inject() { parametersOf(requireActivity()) }
    protected val app: App by lazy { App.getInstance() as App }
    protected val homeActivity: HomeActivity by lazy { activity as HomeActivity }

    protected fun navigateTo(id: Int) = findNavController().navigate(id)
    protected fun navigateTo(id: Int, bundle: Bundle) = findNavController().navigate(id, bundle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding?.lifecycleOwner = this
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivity.invalidateTickerPlayer()

        if (!isOnline(requireContext())) {
            NoInternetDialog().show(childFragmentManager, "no_internet")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                //Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                //Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                //Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}