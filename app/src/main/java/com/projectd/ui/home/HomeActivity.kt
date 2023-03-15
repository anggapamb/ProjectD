package com.projectd.ui.home

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.clearNotification
import com.crocodic.core.extension.snack
import com.permissionx.guolindev.PermissionX
import com.projectd.R
import com.projectd.base.App
import com.projectd.base.activity.BaseActivity
import com.projectd.data.model.Prayer
import com.projectd.databinding.ActivityHomeBinding
import com.projectd.service.fcm.FirebaseMsgService
import com.projectd.service.prayer.PrayerPlayer
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val navHostFragment: NavHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val navController: NavController by lazy { navHostFragment.navController }

    private val prayerPlayer: PrayerPlayer by lazy { (application as App).prayerPlayer }

    private var backPressed: Long = 0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearNotification()

        binding.vPrayerInformation.setOnClickListener { navController.navigate(R.id.actionHomeFragment) }
        //initDailySetup()

        fromNotificationUpdateTask()

        permissionNotification()

        onBackPressedHandle()
    }

    private fun fixedNotification() {
        val intent = Intent()
        when(Build.MANUFACTURER) {
            "xiaomi" -> intent.component= ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
            "oppo" -> intent.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")
            "vivo" -> intent.component = ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
            "Letv" -> intent.component = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")
            "Honor" -> intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
        }

        val list = this.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.size > 0) {
            this.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun permissionNotification() {
        PermissionX.init(this)
            .permissions(Manifest.permission.POST_NOTIFICATIONS)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { allGranted, _, _ ->
                if (!allGranted) {
                    permissionNotification()
                }
            }
    }

    private fun fromNotificationUpdateTask() {
        CoreSession(this).setValue(IS_UPDATE_TASK, intent.getBooleanExtra(FirebaseMsgService.NOTIFICATION_UPDATE_TASK, false))
    }

    /* worker prayer
    private fun initDailySetup() {
        DailySetupWorker.setup(this)
    }
    */

    fun popMsg(msg: String) = binding.root.snack(msg)

    fun playPrayer(prayer: Prayer) {
        prayerPlayer.playPrayer(prayer)
        invalidateTickerPlayer()
    }

    fun isPlayPrayer() = prayerPlayer.isPlayPrayer()

    fun stopPrayer() {
        prayerPlayer.stopPrayer()
        binding.prayer = null
    }

    fun invalidateTickerPlayer() {
        binding.prayer = if (navController.currentDestination?.id != R.id.homeFragment && isPlayPrayer()) prayerPlayer.prayer else null
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun playBackMedia(playBack: Prayer.PlayBack) {
        if (playBack == Prayer.PlayBack.STOP) {
            binding.prayer = null
        }
    }

    private fun onBackPressedHandle() {
        this@HomeActivity
            .onBackPressedDispatcher
            .addCallback(this@HomeActivity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (navController.currentDestination?.id) {
                        R.id.homeFragment -> {
                            if (backPressed + 2000 > System.currentTimeMillis()) {
                                finishAffinity()
                            } else {
                                popMsg("Press back again to quit.")
                            }
                            backPressed = System.currentTimeMillis()
                        }
                        R.id.taskFragment -> {
                            CoreSession(this@HomeActivity).setValue(IS_UPDATE_TASK, false)
                            navController.navigateUp()
                        }
                        else -> {
                            navController.navigateUp()
                        }
                    }
                }
            })
    }

    companion object {
        const val IS_UPDATE_TASK = "is_update_task"
    }
}