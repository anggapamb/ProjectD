package com.projectd.base.observe

import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.data.CoreSession
import com.projectd.api.ApiService
import com.projectd.data.Cons
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class BaseObserver(
    private val apiService: ApiService,
    private val session: CoreSession,
) {
    operator fun invoke(
        block: suspend () -> String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        toast: Boolean = false,
        responseListener: ApiObserver.ResponseListener
    ) {
        ApiObserver(
            block,
            toast,
            responseListener = object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                responseListener.onSuccess(response)
            }

            override suspend fun onError(response: ApiResponse) {
                val status = response.rawResponse?.let { JSONObject(it) }?.getInt("code")
                if (status == ApiCode.EXPIRED) {
                    ApiObserver(
                        { apiService.refreshToken() },
                        responseListener = object : ApiObserver.ResponseListener {
                            override suspend fun onSuccess(response: JSONObject) {
                                val token = response.getString("access_token")
                                session.setValue(Cons.DB.USER.ACCESS_TOKEN, token)
                                ApiObserver(block, responseListener = responseListener)
                            }

                            override suspend fun onError(response: ApiResponse) {
                                responseListener.onError(response)
                            }
                        })
                }
                responseListener.onError(response)
            }

            override suspend fun onExpired(response: ApiResponse) {
                responseListener.onExpired(response)
            }
        })
    }
}