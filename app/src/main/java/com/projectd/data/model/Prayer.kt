package com.projectd.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prayer(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    var like: List<String>? = null,
    val url: String? = null,
    val image: String? = null
) : Parcelable {
    enum class PlayBack { PLAY, PAUSE, STOP }
}