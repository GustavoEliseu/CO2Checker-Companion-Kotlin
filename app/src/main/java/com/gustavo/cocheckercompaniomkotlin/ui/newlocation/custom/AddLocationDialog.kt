package com.gustavo.cocheckercompaniomkotlin.ui.newlocation.custom

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gustavo.cocheckercompaniomkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompanionkotlin.R

class AddLocationDialog(
    val location: NewLocationData,
    val saveNewLocation: (NewLocationData) -> Unit,
    val cancelled: () -> Unit
) : DialogFragment() {


    var nameEditText: TextInputEditText? = null
    var nameTextInput: TextInputLayout? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val view: View? = activity?.layoutInflater?.inflate(R.layout.new_location_dialog, null)
        val alert = AlertDialog.Builder(requireActivity())
        alert.setView(view)

        nameEditText = view?.findViewById(R.id.name_edit_text)
        nameTextInput = view?.findViewById(R.id.name_text_input)

        val dialog: AlertDialog = alert.create()

        view?.findViewById<AppCompatButton>(R.id.btnCancel)?.setOnClickListener {
            cancelled()
            dismiss()
        }

        view?.findViewById<AppCompatButton>(R.id.btnConfirm)?.setOnClickListener {
            when {
                nameEditText?.text.isNullOrEmpty() -> {
                    nameTextInput?.error = getString(R.string.error_sensor_name)
                }

                else -> {
                    nameTextInput?.error = null
                    nameEditText?.text?.let { name ->
                        location.Name = name.toString()
                        if (location.Name.isNotNullOrNotEmptyOrNotBlank()) {
                            saveNewLocation(location)
                            dismiss()
                        }
                    }
                }
            }
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        cancelled()
        super.onDismiss(dialog)
    }
}