package com.projectd.ui.home

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.core.os.BuildCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.crocodic.core.extension.clearNotification
import com.crocodic.core.extension.snack
import com.projectd.R
import com.projectd.base.activity.BaseActivity
import com.projectd.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.navController }

    private var backPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearNotification()

        //binding.vPrayerInformation.setOnClickListener { navController.navigate(R.id.actionHomeFragment) }

        onBackPressedHandle()
    }

    fun popMsg(msg: String) = binding.root.snack(msg)

    private fun onBackPressedHandle() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    if (backPressed + 2000 > System.currentTimeMillis()) {
                        finish()
                    } else {
                        popMsg("Press back again to quit.")
                    }
                    backPressed = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
        } else {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navController.currentDestination?.id == R.id.homeFragment) {
                        if (backPressed + 2000 > System.currentTimeMillis()) {
                            finish()
                        } else {
                            popMsg("Press back again to quit.")
                        }
                        backPressed = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            })
        }
    }
}