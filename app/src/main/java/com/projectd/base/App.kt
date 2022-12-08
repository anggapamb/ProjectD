package com.projectd.base

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.crocodic.core.helper.tree.ReleaseTree
import com.projectd.BuildConfig
import com.projectd.data.Session
import com.projectd.injection.AppModule
import com.projectd.service.prayer.PrayerPlayer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application(), LifecycleEventObserver {

    private val session: Session by inject()
    val prayerPlayer: PrayerPlayer by lazy { PrayerPlayer(applicationContext) }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        startKoin {
            androidContext(this@App)
            modules(listOf(
                AppModule.modules,
                AppModule.viewModelModule,
                AppModule.networkModule
            ))
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}

    companion object {
        var INSTANCE: App? = null
            private set

        fun getInstance(): Context? = INSTANCE
    }
}