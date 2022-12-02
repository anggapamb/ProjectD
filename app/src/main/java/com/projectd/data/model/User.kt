package com.projectd.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("banned")
    val banned: String?,
    @SerializedName("devision")
    val devision: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("id_devision")
    val idDevision: Int?,
    @SerializedName("is_leader")
    val isLeader: String?,
    @SerializedName("nama")
    val nama: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("re_login")
    val reLogin: String?,
    @SerializedName("token")
    val token: String?
): Parcelable {

    fun shortName(): String {
        val spName = nama?.split(" ")
        val name = if ((spName?.size ?: 0) >= 2) {
            "${spName?.get(0)} ${spName?.get(spName.lastIndex)}"
        } else {
            "${spName?.get(0)}"
        }
        return name
    }
}