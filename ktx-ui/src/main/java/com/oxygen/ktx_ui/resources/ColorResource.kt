package com.oxygen.ktx_ui.resources

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import java.io.Serializable

/**
 * @author Iamushev Igor
 * @since  28.4.2022
 */
interface ColorResource : Serializable {

    @ColorInt
    fun getColor(context: Context): Int

    fun darken(context: Context): ColorResource =
        StaticColorResource(
            colorToParse = null,
            color = ColorUtils.blendARGB(getColor(context), Color.BLACK, 0.4f)
        )

}

data class StaticColorResource(
    val colorToParse: String?,
    @ColorInt val color: Int? = null,
) : ColorResource {

    private var cachedColor: Int? = null

    override fun getColor(context: Context): Int = color ?: cachedColor ?: run {
        val parsed = try {
            Color.parseColor(colorToParse)
        } catch (e: IllegalArgumentException) {
            Color.WHITE
        }
        cachedColor = parsed
        parsed
    }

}
