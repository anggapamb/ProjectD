package com.projectd.data.model

import com.google.gson.annotations.SerializedName
import com.projectd.R

data class AdditionalMenu(
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("name_icon")
    val nameIcon: String?,
    val color: Int? = R.color.text_bg_blue,
)