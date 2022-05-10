package com.oxygen.ktx_ui.resources

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import com.oxygen.ktx_ui.context.color
import java.io.Serializable

/**
 * @author Iamushev Igor
 * @since  28.4.2022
 */
fun String.asHexToColor(default: Int = Color.TRANSPARENT): Int =
    try {
        val rawColor = if (this[0] == '#') this else "#$this"
        Color.parseColor(rawColor)
    } catch (e: Exception) {
        default
    }

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

data class AndroidColorResource(@ColorRes val colorRes: Int) : ColorResource {

    private var cachedColor: Int? = null

    private var modifier: (Int) -> Int = { it }

    override fun getColor(context: Context): Int = cachedColor ?: run {
        modifier(context.color(colorRes))
            .also { cachedColor = it }
    }

    fun alpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float) =
        apply { modifier = { ColorUtils.setAlphaComponent(it, (alpha * 255).toInt()) } }
}

object TransparentColorResource : ColorResource {
    override fun getColor(context: Context) = Color.TRANSPARENT
}

fun parseRgbaColor(color: String): ColorResource {
    val rgba = color.substring(1).toLong(16)
    val alpha = rgba shl 24
    val argb = (rgba shr 8) or alpha
    return StaticColorResource(color = argb.toInt(), colorToParse = null)
}
