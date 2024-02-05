package com.gustavo.cocheckercompaniomkotlin.ui.custom

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.gustavo.cocheckercompaniomkotlin.utils.DefaultEnum
import com.gustavo.cocheckercompanionkotlin.R

class OptionsAlertDialog<D : DefaultEnum>(
    val context: Context,
    val title: Int? = null,
    val titleString: String? = null,
    var elements: Array<D>? = null,
    val clickListener: (D) -> Unit,
    val onCancel: () -> Unit = {}
) {

    fun getDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
        title?.let { builder.setTitle(it) }
        titleString?.let { builder.setTitle(titleString) }
        builder.setNegativeButton(R.string.cancel_button) { _, _ -> onCancel() }
        builder.setItems(elements?.map { context.getString(it.nameId) }
            ?.toTypedArray()) { _, which ->
            elements?.getOrNull(which)?.let {
                clickListener(it)
            }
        }

        return builder.create()
    }
}