package com.projectd.util

import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun Fragment.getNavigationResultBoolean(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(key)

fun <T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}