package com.gustavo.cocheckercompaniomkotlin.utils

import android.text.Editable
import android.util.Patterns

fun String?.isNotNullOrNotEmptyOrNotBlank(): Boolean {
    return this.isNullOrEmptyOrBlank().not()
}

fun String?.isNullOrEmptyOrBlank(): Boolean {
    return isNullOrEmpty() || isNullOrBlank()
}

fun Editable?.isNullOrEmptyOrBlank(): Boolean {
    return isNullOrEmpty() || isNullOrBlank()
}

fun String?.getValueOrEmpty(): String {
    return this ?: "testee"
}

fun Editable?.isValidEmail(): Boolean{
    return if (this != null) Patterns.EMAIL_ADDRESS.matcher(this).matches() else false
}
fun String.isValidEmail(): Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun Editable?.isPasswordValid(): Boolean{
    return this != null && this.length >= 8 && this.toString().containsDigit()
}

fun String.isPasswordValid(): Boolean{
    return  this.length >= 8 && this.containsDigit();
}
fun String?.containsDigit(): Boolean {
    var containsDigit = false
    if (this == null || this.isEmpty()) {
        return containsDigit
    } else {
        for (c in this) {
            containsDigit = Character.isDigit(c)
            if (containsDigit == true) {
                break
            }
        }
        return containsDigit
    }
}
