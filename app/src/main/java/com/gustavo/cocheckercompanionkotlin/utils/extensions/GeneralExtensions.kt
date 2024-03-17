package com.gustavo.cocheckercompanionkotlin.utils.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.gustavo.cocheckercompanionkotlin.ui.custom.OptionsAlertDialog
import com.gustavo.cocheckercompanionkotlin.utils.DefaultEnum

fun <D : DefaultEnum> Context.getDialog(
    title: Int? = null,
    titleString: String? = null,
    elements: Array<D>,
    response: (D) -> Unit,
    onCancel: () -> Unit = {}
): AlertDialog {
    return OptionsAlertDialog(
        context = this,
        title = title,
        titleString = titleString,
        elements = elements,
        clickListener = response,
        onCancel = onCancel
    ).getDialog()
}