package com.projectd.service.fcm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.crocodic.core.helper.DateTimeHelper
import com.crocodic.core.helper.log.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.projectd.R
import com.projectd.data.Session
import com.projectd.ui.home.HomeActivity
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class FirebaseMsgService: FirebaseMessagingService() {

    private val session: Session by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("firebase-receive-message: ${message.data}")

        if (session.getUser() == null) return

        if (isBackground(applicationContext)) {
            Timber.d("CekFirebaseMessage: inBackground")
            createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(), message.data["title"] ?: return, message.data["content"] ?: return)
        } else {
            Timber.d("CekFirebaseMessage: inForeground")
            createAppNotification(applicationContext,message.notification?.title ?: return, message.notification?.body ?: return, message.notification?.clickAction)
        }
    }

    companion object {
        fun createAppNotification(context: Context, title: String, content: String, action: String? = null) {
            /*
            val notification = AppNotification(
                title = title,
                content = content,
                action = action
            )
            */

            val pendingIntent: PendingIntent?
            val mainIntent = Intent(context, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(context)

            stackBuilder.addNextIntent(mainIntent)

            pendingIntent = stackBuilder.getPendingIntent(99, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else PendingIntent.FLAG_CANCEL_CURRENT)

            if (pendingIntent == null) return

            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_fire_department_24)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val name = CHANNEL_NAME
                val description = CHANNEL_NAME
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(DateTimeHelper().createAtLong().toInt(), mBuilder.build())

            //EventBus.getDefault().post(notification)
        }

        fun createNotification(context: Context, idNotification: Int, title: String, message: String, bitmap: Bitmap? = null) {

            if (!isBackground(context)) {

                val pendingIntent: PendingIntent?
                val mainIntent = Intent(context, HomeActivity::class.java)

                val stackBuilder = TaskStackBuilder.create(context)

                stackBuilder.addNextIntent(mainIntent)

                pendingIntent = stackBuilder.getPendingIntent(99, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else PendingIntent.FLAG_CANCEL_CURRENT)

                if (pendingIntent == null) return

                val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_local_fire_department_24)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                bitmap?.let {

                    //mBuilder.setLargeIcon(it)

                    val sender = Person.Builder()
                        .setName(title)
                        .setIcon(IconCompat.createWithBitmap(it))
                        .build()

                    NotificationCompat.MessagingStyle(sender)
                        .addMessage(message, Date().time, sender)
                        .setBuilder(mBuilder)
                }

                //val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationManager = NotificationManagerCompat.from(context)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val name = CHANNEL_NAME
                    val description = CHANNEL_NAME
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val channel = NotificationChannel(CHANNEL_ID, name, importance)
                    channel.description = description
                    channel.enableVibration(true)
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(idNotification, mBuilder.build())
            } else {

                val pendingIntent: PendingIntent?
                val mainIntent = Intent(context, HomeActivity::class.java)

                val stackBuilder = TaskStackBuilder.create(context)

                stackBuilder.addNextIntent(mainIntent)

                pendingIntent = stackBuilder.getPendingIntent(99, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else PendingIntent.FLAG_CANCEL_CURRENT)

                if (pendingIntent == null) return

                val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_local_fire_department_24)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                bitmap?.let {

                    //mBuilder.setLargeIcon(it)

                    val sender = Person.Builder()
                        .setName(title)
                        .setIcon(IconCompat.createWithBitmap(it))
                        .build()

                    NotificationCompat.MessagingStyle(sender)
                        .addMessage(message, Date().time, sender)
                        .setBuilder(mBuilder)
                }

                //val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationManager = NotificationManagerCompat.from(context)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val name = CHANNEL_NAME
                    val description = CHANNEL_NAME
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val channel = NotificationChannel(CHANNEL_ID, name, importance)
                    channel.description = description
                    channel.enableVibration(true)
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(idNotification, mBuilder.build())
            }
        }

        fun isBackground(context: Context): Boolean {
            val myProcess = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(myProcess)
            if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return true

            val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return km.isKeyguardLocked
        }

        const val CHANNEL_ID = "ProjectD"
        const val CHANNEL_NAME = "ProjectD Mobile"
    }

}