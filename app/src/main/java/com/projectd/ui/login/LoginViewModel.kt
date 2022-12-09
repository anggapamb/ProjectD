package com.projectd.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.toObject
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.User
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel(private val apiService: ApiService) : BaseViewModel() {

    fun login(email: String?, password: String?) = viewModelScope.launch {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "Please complete from."))
        } else {
            _apiResponse.send(ApiResponse(ApiStatus.LOADING, message = "Logging in..."))
            ApiObserver(
                block = {apiService.login(email, password)},
                toast = true,
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        val message = response.getString("message")
                        val data = response.getJSONObject("data").toObject<User>(gson)
                        saveFirebaseRegId { regId -> data.fcmId = regId }
                        session.saveUser(data)
                        _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        val message = response.rawResponse?.let { JSONObject(it) }?.getString("message")
                        _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = message))
                    }
                }
            )
        }
    }

}