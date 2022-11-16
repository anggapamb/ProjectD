package com.projectd.data.model

import com.projectd.R

data class AdditionalMenu(
    val label: String?,
    val key: String?,
    val background: Int? = R.drawable.bg_round_12_blue,
    val color: Int? = R.color.text_bg_blue,
    val image: String? = null,
    val link: String? = null
)