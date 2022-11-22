package com.projectd.ui.home

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.projectd.R
import com.projectd.base.activity.BaseActivity
import com.projectd.data.Session
import com.projectd.databinding.ActivityHomeBinding
import org.koin.android.ext.android.inject
import timber.log.Timber

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.navController }
    private val session: Session by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (session.getUser() == null) navController.navigate(R.id.actionLoginFragment)
        Timber.d("CheckUser: ${session.getUser()}")

    }
}