package com.projectd.base.viewmodel

import com.crocodic.core.api.ApiObserver
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.extension.toJson
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.projectd.api.FcmApiService
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.User
import com.projectd.data.param.RequestFcm
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

open class BaseViewModel: CoreViewModel(), KoinComponent {

    protected val session: Session by inject()
    protected val gson: Gson by inject()

    protected val fcmApiService = FcmApiService.getInstance()
    var user: User? = session.getUser()

    override fun apiLogout() { }

    override fun apiRenewToken() { }

    fun saveFirebaseRegId(result: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            result(it.result)
        }
    }

    fun getFirebaseRegId(result: (String) -> Unit) {
        session.getString(Cons.DB.USER.FCM_ID).let(result)
    }

    fun sendNotificationToUser(title: String, message: String, type: String? = null) {
        getFirebaseRegId {
            val data = RequestFcm.Data(
                title = title,
                content = message,
                type = type
            )

            sendFcm(it, data)
        }
    }

    private fun sendFcm(toRegId: String, data: RequestFcm.Data) {

        Timber.d("fcm :: send to $toRegId")

        val param = RequestFcm(
            data = data,
            to = toRegId
        )

        ApiObserver(block = { fcmApiService.send(param.toJson(gson)) }, responseListener = object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) { }
        })
    }
}