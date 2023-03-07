package com.projectd.injection

import com.crocodic.core.data.CoreSession
import com.crocodic.core.helper.okhttp.SSLTrust
import com.google.gson.Gson
import com.projectd.BuildConfig
import com.projectd.api.ApiService
import com.projectd.base.observe.BaseObserver
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.service.AudioHelper
import com.projectd.service.receiver.CancelReceiver
import com.projectd.service.receiver.DoneReceiver
import com.projectd.service.receiver.HoldReceiver
import com.projectd.ui.dialog.AbsentDialog.AbsentViewModel
import com.projectd.ui.dialog.LoadingDialog
import com.projectd.ui.dialog.ManagerChooserDialog.ManagerChooserViewModel
import com.projectd.ui.dialog.ProjectChooserDialog.ProjectChooserViewModel
import com.projectd.ui.dialog.TaskByDateReportDialog.TaskByDateReportViewModel
import com.projectd.ui.dialog.TaskChooserDialog.TaskChooserViewModel
import com.projectd.ui.dialog.TaskReportDialog.TaskReportViewModel
import com.projectd.ui.dialog.UpdateProgressDialog.UpdateProgressViewModel
import com.projectd.ui.home.HomeViewModel
import com.projectd.ui.login.LoginViewModel
import com.projectd.ui.project.add.ProjectAddViewModel
import com.projectd.ui.project.list.ProjectViewModel
import com.projectd.ui.task.add.TaskAddViewModel
import com.projectd.ui.task.list.TaskViewModel
import com.projectd.ui.task.pov.TaskPovViewModel
import com.projectd.ui.today.TodayCheckViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object AppModule {

    val modules = module {
        singleOf(::Gson)
        singleOf(::Session)
        singleOf(::CoreSession)
        singleOf(::AudioHelper)

        single { activityContext ->
            LoadingDialog(activityContext.get())
        }
    }

    val viewModelModule = module {
        viewModel { HomeViewModel(get(), get()) }
        viewModel { LoginViewModel(get(), get()) }
        viewModel { ProjectViewModel(get()) }
        viewModel { ManagerChooserViewModel(get(), get()) }
        viewModel { ProjectAddViewModel(get(), get()) }
        viewModel { TaskViewModel(get(), get()) }
        viewModel { ProjectChooserViewModel(get()) }
        viewModel { TaskAddViewModel(get(), get()) }
        viewModel { TaskReportViewModel(get(), get()) }
        viewModel { TaskByDateReportViewModel(get(), get()) }
        viewModel { AbsentViewModel(get(), get()) }
        viewModel { TodayCheckViewModel(get(), get()) }
        viewModel { UpdateProgressViewModel(get(), get()) }
        viewModel { TaskPovViewModel(get(), get()) }
        viewModel { TaskChooserViewModel(get(), get()) }
        viewModel { DoneReceiver.DoneViewModel(get(), get()) }
        viewModel { HoldReceiver.HoldViewModel(get(), get()) }
        viewModel { CancelReceiver.CancelViewModel(get(), get()) }
    }

    val networkModule = module {
        single { retrofitBuilder(retrofitHttpClient()) }
        single { createApiService(get()) }
        single { BaseObserver(get(), get()) }
    }

    private fun retrofitBuilder(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private fun createApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private fun retrofitHttpClient(): OkHttpClient {
        val unsafeTrustManager = SSLTrust().createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)
        val okHttpClient = OkHttpClient().newBuilder()
            .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val session = getKoin().get<Session>()
                val token = session.getString(Cons.DB.USER.ACCESS_TOKEN)
                val fcmId = session.getString(Cons.DB.USER.FCM_ID)
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("device_token", fcmId)
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }

        if (BuildConfig.DEBUG) {
            val interceptors = HttpLoggingInterceptor()
            interceptors.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptors)
        }
        return okHttpClient.build()
    }

    /*
    val modules = module {
        singleOf(::Gson)
        singleOf(::Session)
        singleOf(::CoreSession)

        single { activityContext ->
            LoadingDialog(activityContext.get())
        }

        viewModelOf(::HomeViewModel)
        viewModelOf(::LoginViewModel)
    }

    val networkModule = module {
        factory { provideOkHttpClient() }
        single { provideRetrofit(provideOkHttpClient()) }
    }

    fun provideOkHttpClient(): OkHttpClient {
        val unsafeTrustManager = SSLTrust().createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)
        val okHttpClient = OkHttpClient().newBuilder()
            .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = session.getString(Cons.TOKEN.PREF_TOKEN)
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }

        if (BuildConfig.DEBUG) {
            val interceptors = HttpLoggingInterceptor()
            interceptors.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptors)
        }
        return okHttpClient.build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }*/
}