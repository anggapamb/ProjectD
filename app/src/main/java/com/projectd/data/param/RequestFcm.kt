package com.projectd.data.param

data class RequestFcm(
    val data: Data?,
    val content_available: Boolean? = true,
    val to: String?,
    val priority: String = "high"
) {
    data class Data(
        val title: String?,
        val content: String?,
        val type: String? = null
    )
}