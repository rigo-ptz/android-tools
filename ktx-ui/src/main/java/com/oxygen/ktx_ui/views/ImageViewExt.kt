package com.oxygen.ktx_ui.views

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorRes
import com.oxygen.ktx_ui.context.color

fun ImageView.tintColorRes(@ColorRes id: Int) {
    this.imageTintList = ColorStateList.valueOf(context.color(id))
}

fun ImageView.clearTint() {
    this.imageTintList = null
}
