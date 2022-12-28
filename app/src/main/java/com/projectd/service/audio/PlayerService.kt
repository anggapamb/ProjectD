package com.projectd.service.audio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.crocodic.core.extension.createIntent
import com.projectd.R
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Prayer
import com.projectd.ui.home.HomeActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class PlayerService: Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val session: Session by inject()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        try {

            val prayer = intent?.getParcelableExtra<Prayer>(Cons.BUNDLE.DATA)

            mediaPlayer = MediaPlayer().apply {
                //setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(prayer?.file)
                setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                prepare()
                setOnPreparedListener {
                    val seek = session.getInt(Cons.BUNDLE.PRAYER_SEEK)
                    it.seekTo(seek)
                    it.start()
                }
                setOnCompletionListener {
                    session.setValue(Cons.BUNDLE.PRAYER_SEEK, 0)
                    EventBus.getDefault().postSticky(Prayer.PlayBack.STOP)
                    stopSelf()
                }
            }

            EventBus.getDefault().register(this)

            val homeIntent = createIntent<HomeActivity> {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
                .build()

            startForeground(901, notificationBuilder)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return START_STICKY
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        stopMedia()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun playBackMedia(playBack: Prayer.PlayBack) {
        when (playBack) {
            Prayer.PlayBack.PLAY -> {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            }
            Prayer.PlayBack.PAUSE -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    session.setValue(Cons.BUNDLE.PRAYER_SEEK, mediaPlayer?.currentPosition ?: 0)
                }
            }
            Prayer.PlayBack.STOP -> {}
        }
    }

    private fun stopMedia() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val CHANNEL_ID = "Doa Pagi"
        const val CHANNEL_NAME = "Doa Pagi Taman Media Lestari"
    }
}