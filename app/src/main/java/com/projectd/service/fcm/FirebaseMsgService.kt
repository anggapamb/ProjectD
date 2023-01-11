package com.projectd.service.fcm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.crocodic.core.extension.toList
import com.crocodic.core.helper.BitmapHelper
import com.crocodic.core.helper.DateTimeHelper
import com.crocodic.core.helper.log.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.projectd.R
import com.projectd.data.Session
import com.projectd.data.model.TaskNotification
import com.projectd.service.receiver.CancelReceiver
import com.projectd.service.receiver.DoneReceiver
import com.projectd.service.receiver.HoldReceiver
import com.projectd.service.worker.DailySetupWorker
import com.projectd.ui.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.koin.android.ext.android.inject
import java.io.IOException
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class FirebaseMsgService: FirebaseMessagingService() {

    private val session: Session by inject()
    private val gson: Gson by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("firebase-receive-message: ${message.data}")

        if (session.getUser() == null) return

        if (message.data["type"] == "update_task") {
            if (isBackground(applicationContext)) {
                processedNotification(message.data, applicationContext, gson)
            } else {
                processedNotification(message.data, applicationContext, gson)
            }
        } else if (message.data["type"] == "prayer") {
            val photo = message.data["user_image"]
            val greeting = arrayOf(
                "Good morning ${session.getUser()?.shortName()}, how are you today?",
                "Hello ${session.getUser()?.shortName()}, ready to change the word huh?",
                "Assalamu'alaikum ${session.getUser()?.shortName()}, hope you always healthy today."
            )
            if (photo.isNullOrEmpty()) {
                if (isBackground(applicationContext)) {
                    createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(), message.data["title"] ?: return, greeting.random())
                } else {
                    createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(),message.data["title"] ?: return, greeting.random())
                }
            } else {
                if (isBackground(applicationContext)) {
                    getImage(photo) { bmp ->
                        bmp?.let { bp ->
                            session.setValue(DailySetupWorker.PRAYER_IMG_URL, photo)
                            session.setValue(DailySetupWorker.PRAYER_IMG_BMP, BitmapHelper.encodeToBase64(bp))
                        }
                        createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(), message.data["title"] ?: "null", greeting.random(), bmp)
                    }
                } else {
                    getImage(photo) { bmp ->
                        bmp?.let { bp ->
                            session.setValue(DailySetupWorker.PRAYER_IMG_URL, photo)
                            session.setValue(DailySetupWorker.PRAYER_IMG_BMP, BitmapHelper.encodeToBase64(bp))
                        }
                        createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(), message.data["title"] ?: "null", greeting.random(), bmp)
                    }
                }
            }
        } else {
            if (isBackground(applicationContext)) {
                createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(), message.data["title"] ?: return, message.data["body"] ?: return)
            } else {
                createNotification(applicationContext, DateTimeHelper().createAtLong().toInt(),message.data["title"] ?: return, message.data["body"] ?: return)
            }
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val description = CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            channel.enableVibration(true)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {

        /* notification default system
        fun createAppNotification(title: String, content: String, action: String? = null) {
            val notification = AppNotification(
                title = title,
                content = content,
                action = action
            )

            EventBus.getDefault().post(notification)
        }
        */

        fun createNotification(context: Context, idNotification: Int, title: String, message: String, bitmap: Bitmap? = null) {

            val notificationManager = NotificationManagerCompat.from(context)
            val pendingIntent: PendingIntent?
            val mainIntent = Intent(context, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(mainIntent)

            pendingIntent = stackBuilder.getPendingIntent(99, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else PendingIntent.FLAG_CANCEL_CURRENT)

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

                val sender = Person.Builder()
                    .setName(title)
                    .setIcon(IconCompat.createWithBitmap(it))
                    .build()

                NotificationCompat.MessagingStyle(sender)
                    .addMessage(message, Date().time, sender)
                    .setBuilder(mBuilder)
            }

            notificationManager.notify(idNotification, mBuilder.build())
        }

        fun createNotificationUpdateTask(context: Context, title: String, message: String, idTask: Int) {
            val notificationManager = NotificationManagerCompat.from(context)

            val pendingIntent: PendingIntent?
            val mainIntent = Intent(context, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(mainIntent)

            pendingIntent = stackBuilder.getPendingIntent(99, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT else PendingIntent.FLAG_CANCEL_CURRENT)


            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT } else 0
            val remoteInput = RemoteInput.Builder(RESULT_KEY)
                .setLabel("Type here...")
                .build()


            /* done */
            val replayDone = Intent(context, DoneReceiver::class.java).putExtra(ID_NOTIFICATION, idTask)
            val donePendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), replayDone, flag)
            val doneAction = NotificationCompat.Action.Builder(
                0,
                "Done",
                donePendingIntent
            ).addRemoteInput(remoteInput).build()


            /* hold */
            val replayHold = Intent(context, HoldReceiver::class.java).putExtra(ID_NOTIFICATION, idTask)
            val holdPendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), replayHold, flag)
            val holdAction = NotificationCompat.Action.Builder(
                0,
                "Hold",
                holdPendingIntent
            ).addRemoteInput(remoteInput).build()


            /* cancel */
            val replayCancel = Intent(context, CancelReceiver::class.java).putExtra(ID_NOTIFICATION, idTask)
            val cancelPendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), replayCancel, flag)
            val cancelAction = NotificationCompat.Action.Builder(
                0,
                "Cancel",
                cancelPendingIntent
            ).addRemoteInput(remoteInput).build()

            val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_fire_department_24)
                .setChannelId(CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(doneAction)
                .addAction(holdAction)
                .addAction(cancelAction)
                .setAutoCancel(true)

            notificationManager.notify(idTask, mBuilder.build())
        }

        fun mBuilder(context: Context?, input: String?, type: String?) : NotificationCompat.Builder? {
            val builder = context?.let {
                NotificationCompat.Builder(it, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_local_fire_department_24)
                    .setChannelId(CHANNEL_ID)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle("$type: $input")
                    .setStyle(null)
                    .setAutoCancel(true)
            }
            return builder
        }

        fun processedNotification(data: Map<String, String>, context: Context, gson: Gson) {
            val dataNotification = JSONArray(data["task"]).toList<TaskNotification>(gson)
            dataNotification.forEach {
                it.idTask?.let { idTask ->
                    createNotificationUpdateTask(context, "Reminder Task in ${it.project}", "${it.taskName}", idTask)
                }
            }
        }

        /* change color action text
        private fun getActionText(context: Context, title: String): Spannable {
            val spannable: Spannable = SpannableString(title)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                spannable.setSpan(
                    ForegroundColorSpan(context.getColor(R.color.text_bg_red)), 0, spannable.length, 0
                )
            }
            return spannable
        }
        */

        fun getImage(imageUrl: String, result: (Bitmap?) -> Unit) = runBlocking {
            configureHttps()

            Log.d("image dl :: call $imageUrl")
            val url = URL(imageUrl)

            withContext(Dispatchers.IO) {
                try {
                    Log.d("image dl :: start")
                    val input = url.openStream()
                    BitmapFactory.decodeStream(input)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("image dl :: failed")
                    result(null)
                    null
                }
            }?.let { bitmap ->
                Log.d("image dl :: success")
                result(bitmap)
            }
        }

        private fun configureHttps() {
            val ctx = SSLContext.getInstance("TLS")
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })
            val hostnameVerifier = HostnameVerifier { hostname, session ->
                true
            }

            ctx.init(null, trustAllCerts, null)
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
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
        const val RESULT_KEY = "RESULT_KEY"
        const val ID_NOTIFICATION = "ID_NOTIFICATION_UPDATE_TASK"
    }

}