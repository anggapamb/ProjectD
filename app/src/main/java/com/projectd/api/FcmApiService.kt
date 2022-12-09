package com.projectd.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApiService {

    @POST("send")
    suspend fun send(
        @Body body: String?
    ): String

    companion object {
        private val client: OkHttpClient by lazy {
            val interceptors = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            OkHttpClient().newBuilder()
                .addNetworkInterceptor { chain ->

                    val requestBuilder = chain.request().newBuilder()
                        .header("Content-Type", "application/json")
                        //.header("Authorization", "key=AAAAYZRhrxA:APA91bF_2AuPH-9TaSbqtzkOpszaAKU-oMxYe4xh8o62vyCiPmr9OCkdTeOH_jPtKghCGnpb2_IF4qcyPCb0Wp-qGRT5TYkB3Ku4e2IIvFETF7mz4ntoIJo9obPLZNst1NHdiAHe4MZ-")
                        .method(chain.request().method, chain.request().body)
                        .build()

                    chain.proceed(requestBuilder)
                }
                .addNetworkInterceptor(interceptors)
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build()
        }

        fun getInstance() = retrofit.create(FcmApiService::class.java)
    }
}