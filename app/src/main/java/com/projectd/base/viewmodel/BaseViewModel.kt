package com.projectd.base.viewmodel

import com.crocodic.core.base.viewmodel.CoreViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.projectd.data.Session
import com.projectd.data.model.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: CoreViewModel(), KoinComponent {

    protected val session: Session by inject()
    protected val gson: Gson by inject()

    var user: User? = session.getUser()

    override fun apiLogout() { }

    override fun apiRenewToken() { }
}