package com.projectd.data.model

import android.graphics.drawable.Drawable

data class HomeMenu(
    val icon: Int,
    val color: Int,
    val background: Drawable?,
    val label: String,
    val key: String,
    var count: Int,
    var countBackground: Int
)