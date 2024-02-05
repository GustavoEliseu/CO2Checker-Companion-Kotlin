package com.gustavo.cocheckercompaniomkotlin.utils.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.gustavo.cocheckercompaniomkotlin.ui.custom.OptionsAlertDialog
import com.gustavo.cocheckercompaniomkotlin.utils.DefaultEnum

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