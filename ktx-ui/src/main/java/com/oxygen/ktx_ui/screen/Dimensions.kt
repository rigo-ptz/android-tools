package com.oxygen.ktx_ui.screen

import android.content.res.Resources
import android.util.DisplayMetrics

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
fun Int.dpToPx(): Float {
    val metrics = Resources.getSystem().displayMetrics
    return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.dpToPx(): Float {
    val metrics = Resources.getSystem().displayMetrics
    return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Int.pxToDp() = this / getScale()

fun Float.pxToDp() = this / getScale()

private fun getScale() =
    Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
