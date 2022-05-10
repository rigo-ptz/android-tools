package com.oxygen.ktx_ui.screen

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import androidx.core.view.WindowInsetsCompat

object ScreenUtil {

    fun getScreenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels

    fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

}

/**
 * Returns height of screen without status bar height.
 *
 * @param insets insets that come from [androidx.core.view.OnApplyWindowInsetsListener]
 */
fun Activity.screenHeightPixelsCompat(insets: WindowInsetsCompat): Int {
    val isCutoutAvailable = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            display?.cutout != null
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            windowManager.defaultDisplay.cutout != null
        }
        else -> {
            false
        }
    }

    return if (isCutoutAvailable) {
        // when cutout is available then heightPixels already has subtracted status bar height
        Resources.getSystem().displayMetrics.heightPixels
    } else {
        // when there is no cutout
        val statusBarHeight: Int = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        Resources.getSystem().displayMetrics.heightPixels - statusBarHeight
    }
}
