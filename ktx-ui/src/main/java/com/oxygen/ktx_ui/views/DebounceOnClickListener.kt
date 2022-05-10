package com.oxygen.ktx_ui.views

import android.view.View
import java.util.WeakHashMap

abstract class DebounceOnClickListener constructor(
    private val minimumIntervalMillis: Long = 1_000
) : View.OnClickListener {

    private val lastClickMap: MutableMap<View, Long> = WeakHashMap()

    abstract fun onDebounceClick(v: View)

    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView] ?: 0L
        val currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp - previousClickTimestamp > minimumIntervalMillis) {
            lastClickMap[clickedView] = currentTimestamp
            onDebounceClick(clickedView)
        }
    }
}
