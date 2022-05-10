package com.oxygen.ktx_ui.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

class CenterImageSpan(drawable: Drawable) : ImageSpan(drawable) {

    private var drawableRef: WeakReference<Drawable>? = null

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val drawable = getCachedDrawable()
        canvas.save()
        val transY = (bottom - top) / 2 - drawable.bounds.height() / 2 // Centers the drawable
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }

    /**
     * Copy & paste of private method from [DynamicDrawableSpan].
     */
    private fun getCachedDrawable(): Drawable {
        val wr = drawableRef
        var d: Drawable? = null
        if (wr != null) {
            d = wr.get()
        }
        if (d == null) {
            d = drawable
            drawableRef = WeakReference(d)
            return d
        }
        return d
    }
}
