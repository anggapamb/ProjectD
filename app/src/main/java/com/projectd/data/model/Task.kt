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
    val createdBy: CreatedBy?,
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
    @SerializedName("verifiedBy")
    val verifiedBy: VerifiedBy?,
): Parcelable {

    @Parcelize
    data class ProjectDetail(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("project_name")
        val projectName: String?,
        @SerializedName("project_director")
        val idProjectDirector: Int?
    ): Parcelable

    @Parcelize
    data class CreatedBy(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("photo")
        val photo: String?,
        @SerializedName("devision")
        val devision: Devision?
    ): Parcelable

    @Parcelize
    data class Devision(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("devision_name")
        val devisionName: String?
    ): Parcelable

    @Parcelize
    data class VerifiedBy(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
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