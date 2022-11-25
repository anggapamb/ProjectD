package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import com.projectd.data.Cons
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("createdBy")
    val createdBy: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("difficulty")
    val difficulty: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("project_director")
    val projectDirector: String?,
    @SerializedName("project_name")
    val projectName: String?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
): Parcelable {
    fun prettyTimeline() = "${DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMMM yyyy")} - ${DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMMM yyyy")}"
    fun prettyStartDate() = DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMM yyyy")
    fun prettyEndDate() = DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMM yyyy")

    companion object {
        const val MEDIUM = "medium"
        const val HARD = "hard"
    }
}