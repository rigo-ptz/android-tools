package com.oxygen.ktx_ui.views.scrollview

import android.view.View
import android.widget.ScrollView
import com.oxygen.ktx_ui.screen.dpToPx

fun ScrollView.addLiftOnScrollListener(
    liftedElevationDp: Int,
    loweredElevationDp: Int = 0,
    vararg targetViews: View
) {
    fun getElevation(): Float = if (canScrollUp()) {
        liftedElevationDp.dpToPx()
    } else {
        loweredElevationDp.toFloat()
    }

    viewTreeObserver.addOnScrollChangedListener {
        targetViews.forEach {
            it.elevation = getElevation()
        }
    }
}

fun ScrollView.canScrollUp() = canScrollVertically(-1)
