package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskByDate(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("created_by")
    val createdBy: Int?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("done_at")
    val doneAt: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("load")
    val load: String?,
    @SerializedName("project")
    val project: Int?,
    @SerializedName("projectDetail")
    val projectDetail: ProjectDetail?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("verified")
    val verified: Boolean?,
    @SerializedName("verified_by")
    val verifiedBy: String?,
    @SerializedName("verifiedBy")
    val verifiedBy2: String?
): Parcelable {

    @Parcelize
    data class ProjectDetail(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("project_name")
        val projectName: String?
    ): Parcelable

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