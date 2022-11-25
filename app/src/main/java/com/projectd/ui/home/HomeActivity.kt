package com.projectd.ui.home

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.crocodic.core.extension.snack
import com.projectd.R
import com.projectd.base.activity.BaseActivity
import com.projectd.data.Session
import com.projectd.databinding.ActivityHomeBinding
import org.koin.android.ext.android.inject

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.navController }
    private val session: Session by inject()
    private var backPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (session.getUser() == null) navController.navigate(R.id.actionLoginFragment)

        onBackPressedHandle()
    }

    fun popMsg(msg: String) = binding.root.snack(msg)

    private fun onBackPressedHandle() {
        this
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navController.currentDestination?.id == R.id.homeFragment) {
                        if (backPressed + 2000 > System.currentTimeMillis()) {
                            finishAffinity()
                        } else {
                            popMsg("Press back again to quit.")
                        }
                        backPressed = System.currentTimeMillis()
                    } else {
                        navController.navigateUp()
                    }
                }
            })
    }
}