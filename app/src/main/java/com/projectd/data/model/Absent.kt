package com.projectd.data.model


import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Absent(
    @SerializedName("approved")
    val approved: Boolean?,
    @SerializedName("approvedBy")
    val approvedBy: ApprovedBy?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("reason")
    val reason: String?
): Parcelable {

    @Parcelize
    data class ApprovedBy(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("photo")
        val photo: String?,
        @SerializedName("email")
        val email: String?,
    ): Parcelable

    fun prettyDate() = DateTimeHelper().convert(date, "yyyy-MM-dd", "d MMMM yyyy")

    companion object {
        const val PENDING = 0
        const val APPROVE = true
        const val REJECT = false
    }

}