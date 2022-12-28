package com.projectd.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.service.fcm.FirebaseMsgService.Companion.ID_NOTIFICATION
import com.projectd.service.fcm.FirebaseMsgService.Companion.RESULT_KEY
import com.projectd.service.fcm.FirebaseMsgService.Companion.mBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DoneReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(SupervisorJob())
    private val viewModel: DoneViewModel by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = context?.let { NotificationManagerCompat.from(it) }
        val remoteInput = intent?.let { RemoteInput.getResultsFromIntent(it) }
        val idTask = intent?.getIntExtra(ID_NOTIFICATION, 0)

        if (remoteInput != null) {
            val input = remoteInput.getCharSequence(RESULT_KEY).toString()

            scope.launch {
                viewModel.apiResponse.collect { apiResponse ->
                    if (apiResponse.status == ApiStatus.SUCCESS) {
                        mBuilder(context, input, "Done")?.build()?.let { idTask?.let { it1 -> notificationManager?.notify(it1, it) } }
                    } else {
                        mBuilder(context, apiResponse.message, "Error")?.build()?.let { idTask?.let { it1 -> notificationManager?.notify(it1, it) } }
                    }
                }
            }

            viewModel.updateTask(idTask.toString(), input)
        }
    }

    class DoneViewModel(private val apiService: ApiService): BaseViewModel() {

       fun updateTask(idTask: String, description: String) = viewModelScope.launch {
           ApiObserver(
               block = {apiService.updateTask(idTask, "done", description) },
               toast = false,
               responseListener = object : ApiObserver.ResponseListener {
                   override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.send(ApiResponse(ApiStatus.SUCCESS, message = "Sent!"))
                   }

                   override suspend fun onError(response: ApiResponse) {
                       val msg = response.rawResponse?.let { JSONObject(it) }?.getString("message")
                       _apiResponse.send(ApiResponse(ApiStatus.ERROR, message = msg))
                   }

               }
           )
       }

    }

}