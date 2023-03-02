package com.projectd.data

import android.content.Context
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.toJson
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.projectd.data.model.User

class Session(context: Context, private val gson: Gson): CoreSession(context) {
    fun saveUser(user: User) {
        setValue(PREF_USER, user.toJson(gson))
    }

    fun getUser(): User? {
        getString(PREF_USER).also {
            return if (it.isEmpty()) {
                null
            } else {
                it.toObject<User>(gson)
            }
        }
    }

    fun saveDivisions(divisions: List<User.Devision>) {
        setValue(PREF_DIVISION, divisions.toJson(gson))
    }

    fun getDivisions(): List<User.Devision> {
        getString(PREF_DIVISION).also {
            return if (it.isEmpty()) {
                emptyList()
            } else {
                it.toList(gson)
            }
        }
    }

    fun clearUser() = setValue(PREF_USER, "")
    fun clearDivisions() = setValue(PREF_DIVISION, "")

    companion object {
        const val PREF_USER = "user"
        const val LAST_DATE_SEEK = "last_date_seek"
        const val PREF_DIVISION = "divisions"
    }
}