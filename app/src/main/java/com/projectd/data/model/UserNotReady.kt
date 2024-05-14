package com.projectd.data.model


import com.google.gson.annotations.SerializedName

data class UserNotReady(
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("id_devision")
    val idDevision: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo")
    val photo: String?
)