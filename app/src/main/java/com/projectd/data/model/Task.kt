package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("devision")
    val devision: String?,
    @SerializedName("done_at")
    val doneAt: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_login")
    val idLogin: String?,
    @SerializedName("load")
    val load: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("project")
    val project: String?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("verified")
    val verified: String?,
    @SerializedName("verifiedBy")
    val verifiedBy: String?
) : Parcelable {

    fun prettyDone() = DateTimeHelper().convert(doneAt, "yyyy-MM-dd HH:mm:ss", "d MMMM, HH:mm")

    companion object {
        const val PROCESS = "process"
        const val DONE = "done"
        const val HOLD = "hold"
        const val CANCEL = "cancel"

        const val STANDBY = "standby"
        const val LOW = "low"
        const val MEDIUM = "medium"
        const val HIGH = "high"
    }
}