package com.projectd.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prayer(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("favorit")
    val favorit: Int?,
    @SerializedName("file")
    val `file`: String?,
    @SerializedName("file_name")
    val fileName: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user")
    val user: Int?,
    @SerializedName("userOwner")
    val userOwner: UserOwner?
): Parcelable {

    @Parcelize
    data class UserOwner(
        @SerializedName("email")
        val email: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("photo")
        val photo: String?
    ): Parcelable

    enum class PlayBack { PLAY, PAUSE, STOP }
}