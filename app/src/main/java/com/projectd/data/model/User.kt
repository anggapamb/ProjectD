package com.projectd.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("devision")
    val devision: Devision?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_leader")
    val isLeader: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status_account")
    val statusAccount: String?
): Parcelable {

    @Parcelize
    data class Devision(
        @SerializedName("devision_name")
        val devisionName: String?,
        @SerializedName("id")
        val id: Int?
    ): Parcelable

    fun shortName(): String {
        val spName = name?.split(" ")
        val name = if ((spName?.size ?: 0) >= 2) {
            "${spName?.get(0)} ${spName?.get(spName.lastIndex)}"
        } else {
            "${spName?.get(0)}"
        }
        return name
    }

    fun oneName(): String {
        val spName = name?.split(" ")
        return "${spName?.get(0)}"
    }

}