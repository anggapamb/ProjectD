package com.projectd.injection

import com.google.gson.Gson
import com.projectd.data.Session
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object AppModule {
    val modules = module {
        singleOf(::Gson)
        singleOf(::Session)
        //singleOf(::AudioHelper)

        single { activityContext ->
            //LoadingDialog(activityContext.get())
        }

//        viewModelOf(::LoginViewModel)
//        viewModelOf(::RegisterViewModel)
//        viewModelOf(::HomeViewModel)
//        viewModelOf(::TaskViewModel)
//        viewModelOf(::TaskAddViewModel)
//        viewModelOf(::TaskChooserViewModel)
//        viewModelOf(::TaskReportViewModel)
//        viewModelOf(::ProjectChooserViewModel)
//        viewModelOf(::ProjectViewModel)
//        viewModelOf(::ProjectAddViewModel)
//        viewModelOf(::ManagerChooserViewModel)
//        viewModelOf(::TodayCheckViewModel)
//        viewModelOf(::AbsentViewModel)
//        viewModelOf(::UpdateProgressViewModel)
//        viewModelOf(::TaskPovViewModel)
    }
}