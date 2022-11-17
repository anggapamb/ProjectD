package com.projectd.data.model

import com.crocodic.core.helper.DateTimeHelper
import java.util.*

data class TaskDay(
    val date: String,
    var selected: Boolean = false
) {
    fun dayStr() = DateTimeHelper().convert(date, "yyyy-MM-dd", "EEE")
    fun dateInt() = DateTimeHelper().convert(date, "yyyy-MM-dd", "d")
    fun today() = date == DateTimeHelper().dateNow()
    fun sunday() = Calendar.getInstance().apply { time = DateTimeHelper().toDate(date) }[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY
    fun saturday() = Calendar.getInstance().apply { time = DateTimeHelper().toDate(date) }[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY
}
