package com.gustavo.cocheckercompaniomkotlin.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.getParentActivity

@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && visibility != null) {
        visibility.observe(
            parentActivity,
            Observer { value -> view.visibility = value ?: View.VISIBLE })
    }
}

@BindingAdapter("mutableTextColorReference")
fun setMutableTextColorReference(view: TextView, color: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && color != null) {
        color.observe(parentActivity, Observer { value ->
            view.setTextColor(ContextCompat.getColor(parentActivity, value))
        })
    }
}

@BindingAdapter("mutableTextColor")
fun setMutableTextColor(view: TextView, color: MutableLiveData<Color>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && color != null) {
        color.observe(parentActivity, Observer { value ->
            view.setTextColor(value.toArgb())
        })
    }
}

@BindingAdapter("mutableIsEnabled")
fun setMutableIsEnabled(view: MaterialButton, checked: MutableLiveData<Boolean>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && checked != null) {
        checked.observe(parentActivity, Observer { value ->
            value?.let {
                view.isEnabled = it
            }
        })
    }
}

@BindingAdapter("mutableText")
fun setMutableTextReference(view: TextView, textReference: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && textReference != null) {
        textReference.observe(parentActivity, Observer { value ->
            val text = parentActivity.getString(value)
            view.contentDescription = text
        })
    }
}

@BindingAdapter("mutableReferenceText")
fun setMutableReferenceText(view: TextView, text: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value ->
            value?.let {
                if (it == 0) view.text = ""
                else view.text = parentActivity.getText(it)
            }
        })
    }
}


@BindingAdapter("mutableText")
fun setMutableText(view: TextView, text: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value ->
            view.text = value ?: ""
        })
    }
}
