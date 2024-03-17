package com.gustavo.cocheckercompanionkotlin.utils.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import java.lang.ref.WeakReference

fun TextView.hideKeyboard(context: WeakReference<Context>): Boolean {
    var didHide = false
    if (this.isFocused) {
        val imm =
            context.get()?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
        didHide = true
    }
    return didHide
}