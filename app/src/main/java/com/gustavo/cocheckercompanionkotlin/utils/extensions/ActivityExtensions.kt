package com.gustavo.cocheckercompanionkotlin.utils.extensions

import android.app.Activity
import android.os.Build
import android.widget.Toast

fun Activity.toast(stringId: Int) {
    Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.longToast(messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_LONG).show()
}

fun Activity.isLegacy(): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
}
