package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.google.gson.annotations.SerializedName
import com.projectd.data.Cons
import kotlinx.parcelize.Parcelize

@Parcelize
data class Absent(
    @SerializedName("approved")
    val approved: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_login")
    val idLogin: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("approvedBy")
    val approvedBy: String?
) : Parcelable {

    fun prettyDate() = DateTimeHelper().convert(date, "yyyy-MM-dd", "d MMMM yyyy")

    companion object {
        const val PENDING = ""
        const val APPROVE = "true"
        const val REJECT = "false"
    }
}