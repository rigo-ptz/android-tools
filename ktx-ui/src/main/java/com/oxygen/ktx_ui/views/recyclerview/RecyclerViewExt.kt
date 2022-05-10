package com.oxygen.ktx_ui.views.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.oxygen.ktx_ui.screen.dpToPx

/**
 * Change [targetViews] elevation when RecyclerView scrolled down. If recycler is scrolled to top
 * then elevation is [loweredElevationDp] which is 0f by default.
 */
fun RecyclerView.addLiftOnScrollListener(
    liftedElevationDp: Int,
    loweredElevationDp: Int = 0,
    vararg targetViews: View
) {
    fun getElevation(): Float = if (canScrollUp()) {
        liftedElevationDp.dpToPx()
    } else {
        loweredElevationDp.toFloat()
    }

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            targetViews.forEach {
                it.elevation = getElevation()
            }
            super.onScrolled(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                targetViews.forEach {
                    it.elevation = getElevation()
                }
        }
    })
}

fun RecyclerView.canScrollUp() = canScrollVertically(-1)

fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

