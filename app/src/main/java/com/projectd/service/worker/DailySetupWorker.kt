package com.projectd.service.worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.work.*
import com.crocodic.core.helper.BitmapHelper
import com.crocodic.core.helper.DateTimeHelper
import com.crocodic.core.helper.log.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.Prayer
import com.projectd.service.fcm.FirebaseMsgService
import com.projectd.util.ViewBindingAdapter.Companion.getEmoji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class DailySetupWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams), KoinComponent {

    private val session: Session by inject()

    override fun doWork(): Result {

        Log.d("work outside")

        session.getUser()?.let { user ->

            val greeting = arrayOf(
                "Good morning ${user.shortName()}, how are you today?",
                "Hello ${user.shortName()}, ready to change the word huh?",
                "Assalamu'alaikum ${user.shortName()}, hope you always healthy today."
            )

            val prayer = Prayer(
                "1",
                "Berdoalah kalian semua",
                "Bismillah",
                "http://206.189.40.49:8111/doa/ab1e0a02397ab7aced16039b9c9aadf0.mp3",
                "https://i.pinimg.com/736x/b9/9d/a0/b99da05e7507d78e9c6a7e9aa7ebbe7b.jpg"
            )

            prayer.let { p ->
                Log.d("work inside")

                val message = "${getEmoji(0x1F534)} LIVE ~ ${p.description}"

                if (p.image.isNullOrEmpty()) {
                    FirebaseMsgService.createNotification(applicationContext, Cons.NOTIFICATION.ID_STARTUP, message, greeting.random())
                } else {
                    getImage(p.image) { bmp ->
                        bmp?.let { bp ->
                            session.setValue(PRAYER_IMG_URL, p.image)
                            session.setValue(PRAYER_IMG_BMP, BitmapHelper.encodeToBase64(bp))
                        }
                        FirebaseMsgService.createNotification(applicationContext, Cons.NOTIFICATION.ID_STARTUP, message, greeting.random(), bmp)
                    }
                }
            }

            /*
            Firebase.firestore.collection(Cons.DB.PRAYER.REFF_NAME).document(day).get()
                .addOnSuccessListener {

                    setup(applicationContext)

                    if (it.exists()) {
                        it.toObject(Prayer::class.java)?.let { p ->
                            Log.d("work inside")

                            val message = "${getEmoji(0x1F534)} LIVE ~ ${p.description}"

                            if (p.image.isNullOrEmpty()) {
                                FirebaseMsgService.createNotification(applicationContext, Cons.NOTIFICATION.ID_STARTUP, message, greeting.random())
                            } else {
                                getImage(p.image) { bmp ->
                                    bmp?.let { bp ->
                                        session.setValue(PRAYER_IMG_URL, p.image)
                                        session.setValue(PRAYER_IMG_BMP, BitmapHelper.encodeToBase64(bp))
                                    }
                                    FirebaseMsgService.createNotification(applicationContext, Cons.NOTIFICATION.ID_STARTUP, message, greeting.random(), bmp)
                                }
                            }
                        }
                    }
                }
             */
        }

        return Result.success()
    }

    companion object {
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

        fun setup(context: Context) {
            Log.d("setup work manager")
            val dailySetupWorkerRequest = OneTimeWorkRequestBuilder<DailySetupWorker>()
                .setInitialDelay(timeDiff(), TimeUnit.MILLISECONDS)
                .addTag(TAG)
                .build()

            val wm = WorkManager.getInstance(context)

            wm.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, dailySetupWorkerRequest)
            //WorkManager.getInstance(applicationContext).enqueueUniqueWork(dailySetupWorkerRequest)
        }

        private fun timeDiff(): Long {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()

            // Set Execution around 05:00:00 AM
            dueDate.set(Calendar.HOUR_OF_DAY, 8)
            dueDate.set(Calendar.MINUTE, 2)
            dueDate.set(Calendar.SECOND, 0)

            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }

            return dueDate.timeInMillis - currentDate.timeInMillis
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

        const val TAG = "daily-setup"
        const val PRAYER_IMG_BMP = "prayer-img-bmp"
        const val PRAYER_IMG_URL = "prayer-img-url"
    }
}