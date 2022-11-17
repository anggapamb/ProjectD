package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.data.Cons
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    val id: String? = null,
    val name: String? = null,
    val pd: User? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val level: Int? = MEDIUM,
    var progresses: List<Progress>? = null,
    var timeline: List<Timeline>? = null,
    val createdAt: String? = DateTimeHelper().createAt(),
    val createdBy: String? = null
): Parcelable {
    fun prettyCreateAt() = DateTimeHelper().convert(createdAt, "yyyy-MM-dd HH:mm:ss", "d MMMM yyyy")
    fun prettyTimeline() = "${DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMMM yyyy")} - ${DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMMM yyyy")}"
    fun prettyStartDate() = DateTimeHelper().convert(startDate, "yyyy-MM-dd", "d MMM yyyy")
    fun prettyEndDate() = DateTimeHelper().convert(endDate, "yyyy-MM-dd", "d MMM yyyy")

    fun prettyTimeline(role: String?, division: String?): String {
        if (role == Cons.ROLE.ROOT || role == Cons.ROLE.MANAGER) {
            return prettyTimeline()
        }

        division?.let {
            timeline?.forEach { tl ->
                if (tl.division == division) {
                    return "${DateTimeHelper().convert(tl.startDate, "yyyy-MM-dd", "d MMMM yyyy")} - ${DateTimeHelper().convert(tl.endDate, "yyyy-MM-dd", "d MMMM yyyy")}"
                }
            }
        }

        return "Unset for your division ($division)"
    }

    fun progress(role: String?, division: String?): Int {
        var xAll = 0
        progresses?.forEach {
            xAll += it.progress ?: 0
        }

        val avg = xAll.toFloat() / (progresses?.size?.toFloat() ?: 0F)

        if (role == Cons.ROLE.ROOT || role == Cons.ROLE.MANAGER) {
            return avg.toInt()
        }

        division?.let {
            progresses?.forEach { tl ->
                if (tl.division == division) {
                    return tl.progress ?: 0
                }
            }
        }

        return 0
    }

    fun updateProgress(division: String?, progress: Int?, username: String?) {
        division?.let {
            var exist = false
            progresses?.forEach { tl ->
                if (tl.division == division) {
                    tl.progress = progress
                    tl.updatedBy = username
                    exist = true
                    return@forEach
                }
            }

            if (!exist) {
                progresses = ArrayList(progresses ?: emptyList()).apply {
                    add(Progress(division, progress, username))
                }
            }
        }
    }

    @Parcelize
    data class Timeline(
        val division: String? = null,
        val startDate: String? = null,
        val endDate: String? = null
    ): Parcelable

    @Parcelize
    data class Progress(
        val division: String? = null,
        var progress: Int? = 0,
        var updatedBy: String? = null
    ): Parcelable

    @Parcelize
    data class User(val username: String? = null, val name: String? = null): Parcelable {
        fun shortName(): String {
            val spName = name?.split(" ")
            /*var name = ""
            spName?.forEachIndexed { index, s ->
                if (index == 2) return name
                if (index > 0) name += " "
                name += s
            }*/

            val name = if ((spName?.size ?: 0) >= 2) {
                "${spName?.get(0)} ${spName?.get(spName.lastIndex)}"
            } else {
                "${spName?.get(0)}"
            }
            return name
        }

        fun firstName(): String? {
            return name?.split(" ")?.get(0)
        }
    }

    companion object {
        const val MEDIUM = 2
        const val HIGH = 4
    }
}