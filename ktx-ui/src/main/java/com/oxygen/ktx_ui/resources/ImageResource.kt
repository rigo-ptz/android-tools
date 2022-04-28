package com.oxygen.ktx_ui.resources

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.oxygen.ktx_ui.context.drawable
import java.io.Serializable

/**
 * @author Iamushev Igor
 * @since  28.4.2022
 */
sealed class ImageResource : Serializable {

    abstract fun getDrawable(context: Context): Drawable

    abstract fun loadTo(imageView: ImageView)

    @Composable
    abstract fun painter(): Painter

}

object EmptyImageResource : ImageResource() {
    override fun getDrawable(context: Context): Drawable = ColorDrawable(Color.TRANSPARENT)

    override fun loadTo(imageView: ImageView) = Unit

    @Composable
    override fun painter(): Painter = object : Painter() {
        override val intrinsicSize: Size get() = Size.Unspecified
        override fun DrawScope.onDraw() {}
    }

}

data class AndroidImageResource(
    val resId: Int,
    val loadCallback: ImageLoadCallback? = null
) : ImageResource() {

    override fun getDrawable(context: Context) =
        context.drawable(resId) ?: error("Cannot find drawable with $resId")

    override fun loadTo(imageView: ImageView) {
        imageView.setImageResource(resId)
        loadCallback?.onImageLoaded()
    }

    @Composable
    override fun painter(): Painter = painterResource(id = resId)

}

interface ImageLoadCallback {
    fun onImageLoaded()
}
