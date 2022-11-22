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
        ApiObserver(
            block = {apiService.login(email, password)},
            toast = false,
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val message = response.getString("message")
                    val data = response.getJSONObject("data").toObject<User>(gson)
                    session.saveUser(data)
                    _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = message))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = "error"))
                }
            }
        )
    }

}