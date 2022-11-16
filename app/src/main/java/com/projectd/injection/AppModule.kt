package com.projectd.injection

import com.google.gson.Gson
import com.projectd.data.Session
import com.projectd.ui.dialog.LoadingDialog
import com.projectd.ui.home.HomeViewModel
import com.projectd.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object AppModule {
    val modules = module {
        singleOf(::Gson)
        singleOf(::Session)
        //singleOf(::AudioHelper)

        single { activityContext ->
            LoadingDialog(activityContext.get())
        }

        viewModelOf(::HomeViewModel)
        viewModelOf(::LoginViewModel)
    }
}