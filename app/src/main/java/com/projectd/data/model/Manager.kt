package com.projectd.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Manager(
    @SerializedName("app_version")
    val appVersion: Int?,
    @SerializedName("banned")
    val banned: String?,
    @SerializedName("devision")
    val devision: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fcm_id")
    val fcmId: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_leader")
    val isLeader: String?,
    @SerializedName("last_online")
    val lastOnline: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("namePhoto")
    val namePhoto: String?,
    @SerializedName("online")
    val online: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("re_login")
    val reLogin: String?,
    @SerializedName("special_access")
    val specialAccess: String?
): Parcelable {

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