package com.projectd.service.prayer

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Prayer
import org.greenrobot.eventbus.EventBus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PrayerPlayer(private val context: Context): KoinComponent {

    private val session: Session by inject()

    var prayer: Prayer? = null
    private var mediaPlayer: MediaPlayer? = null

    fun playPrayer(prayer: Prayer) {
        this.prayer = prayer

        mediaPlayer = MediaPlayer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer?.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build())
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        try {
            mediaPlayer?.setOnPreparedListener {
                val seek = session.getInt(Cons.BUNDLE.PRAYER_SEEK)
                it.seekTo(seek)
                it.start()
            }
            mediaPlayer?.setOnCompletionListener {
                session.setValue(Cons.BUNDLE.PRAYER_SEEK, 0)
                session.setValue(Cons.BUNDLE.PRAYER_COUNT, true)
                EventBus.getDefault().postSticky(Prayer.PlayBack.STOP)
                stopPrayer()
            }
            mediaPlayer?.setDataSource(prayer.file)
            mediaPlayer?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }


            /*.apply {
            //setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(prayer.url)
            setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build())
            prepare()
            setOnPreparedListener {
                val seek = session.getInt(Cons.BUNDLE.PRAYER_SEEK)
                it.seekTo(seek)
                it.start()
            }
            setOnCompletionListener {
                session.setValue(Cons.BUNDLE.PRAYER_SEEK, 0)
                session.setValue(Cons.BUNDLE.PRAYER_COUNT, true)
                EventBus.getDefault().postSticky(Prayer.PlayBack.STOP)
                stopPrayer()
            }*/
    }

    fun isPlayPrayer() = mediaPlayer?.isPlaying ?: false

    fun stopPrayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        hideNotification()
    }

    fun showNotification() {
        if (isPlayPrayer()) {
            val serviceIntent = Intent(context, PrayerNotificationService::class.java).apply {
                putExtra(Cons.BUNDLE.DATA, prayer)
            }
            context.startService(serviceIntent)
        }
    }

    fun hideNotification() {
        context.stopService(Intent(context, PrayerNotificationService::class.java))
    }
}