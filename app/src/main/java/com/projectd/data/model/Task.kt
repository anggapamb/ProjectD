package com.projectd.data.model

import android.os.Parcelable
import com.crocodic.core.helper.DateTimeHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: String? = null,
    val date: String? = DateTimeHelper().dateNow(),
    val description: String? = null,
    val engineer: User? = null,
    val project: Project? = null,
    val level: Int? = LOW,
    var doneAt: String? = null,
    val createdAt: String? = DateTimeHelper().createAt(),
    var updatedAt: String? = null,
    var createdBy: String? = null,
    var status: Int? = PROCESS,
    var notes: String? = null,
    var continueFrom: String? = null,
    var verifiedTask: User? = null,
    var verifiedDone: User? = null,
    val division: String? = null
) : Parcelable {

    fun sortDate() = updatedAt ?: createdAt

    fun prettyDone() = DateTimeHelper().convert(doneAt, "yyyy-MM-dd HH:mm:ss", "d MMMM, HH:mm")

    fun verified(): User? {
        return if (status == DONE) verifiedDone else verifiedTask
    }

    @Parcelize
    data class User(val username: String? = null, val name: String? = null, val photo: String? = null, val role: String? = null) : Parcelable {
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

    @Parcelize
    data class Project(val id: String? = null, val name: String? = null): Parcelable

    companion object {
        const val PROCESS = 1
        const val DONE = 2
        const val HOLD = 3
        const val CANCEL = 4

        const val STANDBY = 1
        const val LOW = 2
        const val MEDIUM = 3
        const val HIGH = 4
    }
}