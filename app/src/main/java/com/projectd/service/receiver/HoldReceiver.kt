package com.projectd.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.projectd.service.fcm.FirebaseMsgService
import com.projectd.service.fcm.FirebaseMsgService.Companion.ID_NOTIFICATION_TASK
import com.projectd.service.fcm.FirebaseMsgService.Companion.RESULT_KEY
import com.projectd.service.fcm.FirebaseMsgService.Companion.mBuilder

class HoldReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationManager = p0?.let { NotificationManagerCompat.from(it) }
        val remoteInput = p1?.let { RemoteInput.getResultsFromIntent(it) }

        if (remoteInput != null) {
            val input = remoteInput.getCharSequence(RESULT_KEY).toString()
            Toast.makeText(p0, "Hold : $input", Toast.LENGTH_SHORT).show()

            mBuilder(p0, input, "Hold")?.build()?.let { notificationManager?.notify(ID_NOTIFICATION_TASK, it) }
        }
    }

}