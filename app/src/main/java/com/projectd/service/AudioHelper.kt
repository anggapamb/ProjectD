package com.projectd.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.projectd.data.Cons
import com.projectd.data.model.Prayer
import com.projectd.service.audio.PlayerService
import org.greenrobot.eventbus.EventBus

class AudioHelper(private val context: Context) {

    fun playMedia(prayer: Prayer) {
        if (isActiveMedia()) {
            EventBus.getDefault().post(Prayer.PlayBack.PLAY)
        } else {
            val serviceIntent = Intent(context, PlayerService::class.java).apply {
                putExtra(Cons.BUNDLE.DATA, prayer)
            }
            context.startService(serviceIntent)
        }
    }

    fun stopMedia() {
        context.stopService(Intent(context, PlayerService::class.java))
    }

    fun pauseMedia() {
        EventBus.getDefault().post(Prayer.PlayBack.PAUSE)
    }

    fun isActiveMedia(): Boolean {
        val manager =  context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (PlayerService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }
}