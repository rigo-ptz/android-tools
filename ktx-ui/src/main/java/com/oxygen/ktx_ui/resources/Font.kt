package com.oxygen.ktx_ui.resources

import android.content.Context
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat

fun Context.getFont(@FontRes fontId: Int) =
    ResourcesCompat.getCachedFont(this, fontId) ?: ResourcesCompat.getFont(this, fontId)!!
