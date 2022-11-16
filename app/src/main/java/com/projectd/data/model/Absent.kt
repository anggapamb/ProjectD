package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.data.Cons
import kotlinx.parcelize.Parcelize

@Parcelize
data class Absent(
    val id: String? = null,
    val date: String? = DateTimeHelper().dateNow(),
    val description: String? = null,
    val engineer: User? = null,
    val createdAt: String? = DateTimeHelper().createAt(),
    var updatedAt: String? = null,
    var status: Int? = PENDING,
    var approver: User? = null,
    val division: String? = Cons.ROLE.ENGINEER
) : Parcelable {

    @Parcelize
    data class User(val username: String? = null, val name: String? = null, val photo: String? = null) : Parcelable {
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
    }

    fun sortDate() = updatedAt ?: createdAt

    fun prettyDate() = DateTimeHelper().convert(date, "yyyy-MM-dd", "d MMMM yyyy")

    companion object {
        const val PENDING = 1
        const val APPROVE = 2
        const val REJECT = 3
    }
}