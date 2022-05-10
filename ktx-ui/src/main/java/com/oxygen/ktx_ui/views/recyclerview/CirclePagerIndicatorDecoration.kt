package com.oxygen.ktx_ui.views.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oxygen.ktx_ui.screen.dpToPx
import kotlin.math.max

/**
 * https://stackoverflow.com/a/46686665
 * https://blog.davidmedenjak.com/android/2017/06/24/viewpager-recyclerview.html
 */
class CirclePagerIndicatorDecoration(
    private val activeColor: Int = Color.BLACK,
    private val inactiveColor: Int = Color.GRAY,
    private val indicatorHeight: Int = 16.dpToPx().toInt(),
    private val indicatorStrokeWidth: Float = 1.dpToPx(),
    private val indicatorItemLength: Float = 8.dpToPx(),
    private val indicatorItemPadding: Float = 8.dpToPx(),
    private val interpolator: Interpolator = AccelerateDecelerateInterpolator()
) : RecyclerView.ItemDecoration() {

    private val paint: Paint = Paint()

    init {
        paint.strokeWidth = indicatorStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val adapter = parent.adapter ?: return

        val itemCount: Int = adapter.itemCount

        // center horizontally, calculate width and subtract half from center
        val totalLength = indicatorItemLength * itemCount
        val paddingBetweenItems = max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX: Float = (parent.width - indicatorTotalWidth) / 2f

        // center vertically in the allotted space
        val indicatorPosY: Float = parent.height - indicatorHeight / 2f
        drawInactiveIndicators(canvas, indicatorStartX, indicatorPosY, itemCount)

        // find active page (which should be highlighted)
        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        val activePosition: Int = layoutManager.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        // find offset of active page (if the user is scrolling)
        layoutManager.findViewByPosition(activePosition)?.let { activeChild ->
            val left: Int = activeChild.left
            val width: Int = activeChild.width

            // on swipe the active item will be positioned from [-width, 0]
            // interpolate offset for smooth animation
            val progress: Float = interpolator.getInterpolation(left * -1 / width.toFloat())
            drawHighlights(canvas, indicatorStartX, indicatorPosY, activePosition, progress)
        }
    }

    private fun drawInactiveIndicators(
        canvas: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        setupInactivePaint()

        // width of item indicator including padding
        val itemWidth = indicatorItemLength + indicatorItemPadding
        var start = indicatorStartX
        for (i in 0 until itemCount) {
            canvas.drawCircle(start, indicatorPosY, indicatorItemLength / 2f, paint)
            start += itemWidth
        }
    }

    // outlined shape
    private fun setupInactivePaint() {
        paint.color = inactiveColor
        paint.style = Paint.Style.STROKE
    }

    private fun drawHighlights(
        canvas: Canvas, indicatorStartX: Float, indicatorPosY: Float,
        highlightPosition: Int, progress: Float
    ) {
        setupActivePaint()

        // width of item indicator including padding
        val itemWidth = indicatorItemLength + indicatorItemPadding
        if (progress == 0f) {
            // no swipe, draw a normal indicator
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            canvas.drawCircle(highlightStart, indicatorPosY, indicatorItemLength / 2f, paint)
        } else {
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            // calculate partial highlight
            val partialLength = indicatorItemLength * progress + indicatorItemPadding * progress
            canvas.drawCircle(highlightStart + partialLength, indicatorPosY, indicatorItemLength / 2f, paint)
        }
    }

    // filled shape
    private fun setupActivePaint() {
        paint.color = activeColor
        paint.style = Paint.Style.FILL
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }

}
