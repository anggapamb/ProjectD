package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: User?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("difficulty")
    val difficulty: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("progress")
    val progress: Int?,
    @SerializedName("projectDirector")
    val projectDirector: User?,
    @SerializedName("project_name")
    val projectName: String?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("timelines")
    val timelines: List<Timeline?>,
    @SerializedName("updated_at")
    val updatedAt: String?
): Parcelable {

    @Parcelize
    data class User(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
    ): Parcelable

    @Parcelize
    data class Timeline(
        @SerializedName("devision_id")
        val devisionId: Int?,
        @SerializedName("end_date")
        val endDate: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("start_date")
        val startDate: String?
    ): Parcelable

    fun prettyTimeline() = "${DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMMM yyyy")} - ${DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMMM yyyy")}"
    fun prettyStartDate() = DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMM yyyy")
    fun prettyEndDate() = DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMM yyyy")

    companion object {
        const val MEDIUM = "medium"
        const val HARD = "hard"
    }

}