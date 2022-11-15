package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.data.Cons
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Long? = null,
    val username: String? = null,
    val name: String? = null,
    val photo: String? = null,
    val phone: String? = null,
    var password: String? = null,
    var fcmId: String? = null,
    var online: Boolean? = false,
    var lastOnline: String? = null,
    val division: String? = null,
    val role: String? = Cons.ROLE.ENGINEER,
    val banned: Boolean? = false,
    val reLogin: Boolean? = false,
    val specialAccess: List<String>? = emptyList(),
    var appVersion: Long? = 1
): Parcelable {
    fun lastSeen(): String {
        return if (DateTimeHelper().isToday(lastOnline)) {
            DateTimeHelper().convert(lastOnline, "yyyy-MM-dd HH:mm:ss", "HH:mm")
        } else {
            DateTimeHelper().convert(lastOnline, "yyyy-MM-dd HH:mm:ss", "EEE, d MMM HH:mm")
        }
    }

    fun shortName(): String {
        val spName = name?.split(" ")
        val name = if ((spName?.size ?: 0) >= 2) {
            "${spName?.get(0)} ${spName?.get(spName.lastIndex)}"
        } else {
            "${spName?.get(0)}"
        }

        /*spName?.forEachIndexed { index, s ->
            if (index == 2) return name
            if (index > 0) name += " "
            name += s
        }*/
        return name
    }
}