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
    @SerializedName("timeline")
    val timeline: Timeline?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("progress")
    val progress: String,
): Parcelable {
    fun prettyTimeline() = "${DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMMM yyyy")} - ${DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMMM yyyy")}"
    fun prettyStartDate() = DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMM yyyy")
    fun prettyEndDate() = DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMM yyyy")

    companion object {
        const val MEDIUM = "medium"
        const val HARD = "hard"
    }
}

@Parcelize
data class Timeline(
    @SerializedName("mobile")
    val mobile: Mobile?,
    @SerializedName("tester")
    val tester: Tester?,
    @SerializedName("web")
    val web: Web?,
    @SerializedName("analyst")
    val analyst: Analyst?,
    @SerializedName("marketing")
    val marketing: Marketing?,
    @SerializedName("psdm")
    val psdm: Psdm?,
    @SerializedName("super_admin")
    val super_admin: SuperAdmin?,
    @SerializedName("manager")
    val manager: ProjectDirector?
): Parcelable

@Parcelize
data class Mobile(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class Tester(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class Web(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class Analyst(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class Marketing(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class Psdm(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class SuperAdmin(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable

@Parcelize
data class ProjectDirector(
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("start_date")
    val startDate: String?
): Parcelable