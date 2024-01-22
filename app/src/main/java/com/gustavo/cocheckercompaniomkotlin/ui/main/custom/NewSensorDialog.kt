package com.gustavo.cocheckercompaniomkotlin.ui.main.custom

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.utils.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompanionkotlin.R

class NewSensorDialog(
    val sensor: NewSensorData?,
    val fabClickListener: (NewSensorData?) -> Unit,
    val clickListener: (NewSensorData?) -> Unit
) : DialogFragment() {

    var nameEditText: TextInputEditText? = null
    var nameTextInput: TextInputLayout? = null
    var macEditText: TextInputEditText? = null
    var macTextInput: TextInputLayout? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {

        val view: View? = activity?.layoutInflater?.inflate(R.layout.new_sensor_dialog, null)
        val alert = AlertDialog.Builder(requireActivity())
        alert.setView(view)

        val qrButton = view?.findViewById<FloatingActionButton>(R.id.readQrButton)

        nameEditText = view?.findViewById(R.id.name_edit_text)
        nameTextInput = view?.findViewById(R.id.name_text_input)
        macEditText = view?.findViewById(R.id.mac_edit_text)
        macTextInput = view?.findViewById(R.id.mac_text_input)


        qrButton?.setOnClickListener {
            fabClickListener(NewSensorData("",  nameEditText?.text.toString()))
            dismiss()
        }

        val dialog: AlertDialog = alert.create()
        sensor?.let {
            nameEditText?.setText(it.name)
            macEditText?.setText(it.mac)
            macEditText?.inputType = InputType.TYPE_NULL
        }


        view?.findViewById<AppCompatButton>(R.id.btnCancel)?.setOnClickListener {
            clickListener(null)
            dismiss()
        }

        view?.findViewById<AppCompatButton>(R.id.btnConfirm)?.setOnClickListener {
            when {
                nameEditText?.text.isNullOrEmpty() -> {
                    nameTextInput?.error = getString(R.string.error_sensor_name)
                }

                macEditText?.text.isNullOrEmpty() -> {
                    macTextInput?.error = getString(R.string.error_sensor_mac)
                }

                else -> {
                    var mySensor: NewSensorData? = null
                    nameTextInput?.error = null
                    macTextInput?.error = null
                    macEditText?.text?.let { mac ->
                        nameEditText?.text?.let { name ->
                            mySensor = NewSensorData(mac.toString(), name.toString())
                        }
                    }
                    mySensor?.let {
                        if (it.mac.isNotNullOrNotEmptyOrNotBlank() && it.name.isNotNullOrNotEmptyOrNotBlank()) {
                            clickListener(it)
                            dismiss()
                        }
                    }
                }
            }
        }
        return dialog
    }
}