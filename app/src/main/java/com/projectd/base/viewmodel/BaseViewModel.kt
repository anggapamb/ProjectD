package com.projectd.base.viewmodel

import com.crocodic.core.base.viewmodel.CoreViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.projectd.data.Session
import com.projectd.data.model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: CoreViewModel(), KoinComponent {

    protected val session: Session by inject()
    protected val gson: Gson by inject()

    override fun apiLogout() { }

    override fun apiRenewToken() { }

    fun saveFirebaseRegId(result: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            result(it.result)
        }
    }
}