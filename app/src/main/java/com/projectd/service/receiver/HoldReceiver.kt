package com.projectd.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput
import com.projectd.service.fcm.FirebaseMsgService.Companion.RESULT_KEY

class HoldReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val remoteInput = p1?.let { RemoteInput.getResultsFromIntent(it) }
        if (remoteInput != null) {
            val input = remoteInput.getCharSequence(RESULT_KEY).toString()
            Toast.makeText(p0, "Hold : $input", Toast.LENGTH_SHORT).show()
        }
    }

}