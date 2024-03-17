package com.gustavo.cocheckercompanionkotlin.utils.extensions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget

@SuppressLint("CheckResult")
fun ImageView.load(
    url: String?,
    isRound: Boolean = false
) {
    if (url.isNullOrEmptyOrBlank()) {
        this.setImageResource(0)
        return
    }
    val glide = Glide.with(context).asBitmap().load(url)

    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    glide.placeholder(circularProgressDrawable)

    glide.apply(myRequestOptionGlide())
    glide.centerCrop()
    if (isRound) {
        glide.into(object : BitmapImageViewTarget(this) {
            override fun setResource(resource: Bitmap?) {
                val circular = RoundedBitmapDrawableFactory
                    .create(
                        context.resources, resource
                    )
                circular.isCircular = true
                circular.cornerRadius = 200.toFloat()

                this.setDrawable(circular)
            }
        })
    } else {
        glide.into(this)
    }
}

@SuppressLint("CheckResult")
private fun myRequestOptionGlide() = RequestOptions().apply {
    skipMemoryCache(false) //TODO CHANGE TO FALSE
    diskCacheStrategy(DiskCacheStrategy.ALL)
}