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
    val favorit: String?,
    @SerializedName("file")
    val `file`: String?,
    @SerializedName("file_name")
    val fileName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name_photo")
    val namePhoto: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user")
    val user: String?
) : Parcelable {
    enum class PlayBack { PLAY, PAUSE, STOP }
}