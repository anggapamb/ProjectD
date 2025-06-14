package com.projectd.service.prayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.crocodic.core.extension.createIntent
import com.crocodic.core.helper.BitmapHelper
import com.projectd.R
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Prayer
import com.projectd.service.worker.DailySetupWorker
import com.projectd.ui.home.HomeActivity
import org.koin.android.ext.android.inject

class PrayerNotificationService: Service() {

    private val session: Session by inject()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {

            val prayer = intent?.getParcelableExtra<Prayer>(Cons.BUNDLE.DATA)

            val image = prayer?.userOwner?.photo

            if (image.isNullOrEmpty()) {
                startNotification(prayer)
            } else {
                val imgUrl = session.getString(DailySetupWorker.PRAYER_IMG_URL)
                val imgBmp = session.getString(DailySetupWorker.PRAYER_IMG_BMP)

                if (imgUrl == prayer.userOwner.photo && imgBmp.isNotEmpty()) {
                    startNotification(prayer, BitmapHelper.decodeBase64(applicationContext, imgBmp))
                } else {
                    DailySetupWorker.getImage(image) {
                        it?.let { bp ->
                            session.setValue(DailySetupWorker.PRAYER_IMG_URL, prayer.userOwner.photo)
                            session.setValue(DailySetupWorker.PRAYER_IMG_BMP, BitmapHelper.encodeToBase64(bp))
                        }
                        startNotification(prayer, it)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return START_STICKY
    }

    private fun startNotification(prayer: Prayer?, bitmap: Bitmap? = null) {
        val homeIntent = createIntent<HomeActivity> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val openPendingIntent = PendingIntent.getActivity(applicationContext, 1204, homeIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT)

        createChannel()

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            //.setContentTitle(prayer?.user)
            .setContentTitle(getString(R.string.label_lestari_taman_media))
            .setContentText(prayer?.description)
            .setSmallIcon(R.drawable.ic_baseline_voicemail_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(openPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)

        bitmap?.let {
            notificationBuilder.setLargeIcon(it)
        }

        startForeground(901, notificationBuilder.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = CHANNEL_NAME
                importance = NotificationManager.IMPORTANCE_HIGH
            }

            applicationContext.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "Doa Pagi"
        const val CHANNEL_NAME = "Doa Pagi Taman Media Lestari"
    }
}