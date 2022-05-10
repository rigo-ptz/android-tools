package com.oxygen.ktx_ui.views

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.oxygen.ktx_ui.screen.ScreenUtil

fun <V : View> BottomSheetBehavior<V>.updatePeekHeightOnLayoutChange(
    view: View,
    maxHeight: Int = ScreenUtil.getScreenHeight(),
    onPeekHeightUpdated: (Int) -> Unit = {}
) {
    view.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
        val height = v.height.coerceAtMost(maxHeight)
        if (height != peekHeight) {
            setPeekHeight(height, true)
            onPeekHeightUpdated(height)
        }
    }
}

/**
 * Returns true if bottom sheet is in [BottomSheetBehavior.STATE_COLLAPSED] state
 */
fun <V : View> BottomSheetBehavior<V>.isCollapsed() = state == BottomSheetBehavior.STATE_COLLAPSED

/**
 * Returns true if bottom sheet is in [BottomSheetBehavior.STATE_EXPANDED] state
 */
fun <V : View> BottomSheetBehavior<V>.isExpanded() = state == BottomSheetBehavior.STATE_EXPANDED

/**
 * Expands the bottom sheet if it's collapsed, collapses the bottom sheet in other cases.
 */
fun <V : View> BottomSheetBehavior<V>.toggleCollapsedExpanded() {
    state =
        if (isCollapsed()) BottomSheetBehavior.STATE_EXPANDED
        else BottomSheetBehavior.STATE_COLLAPSED
}

/**
 * Collapse the bottom sheet by setting it's state.
 *
 * @param hideable Indicates whether it is possible to hide while the sheet is visible.
 */
fun <V : View> BottomSheetBehavior<V>.collapse(hideable: Boolean = true) {
    isHideable = hideable
    state = BottomSheetBehavior.STATE_COLLAPSED
}

/**
 * Hide the bottom sheet by setting it's state.
 */
fun <V : View> BottomSheetBehavior<V>.hide() {
    isHideable = true
    state = BottomSheetBehavior.STATE_HIDDEN
}
