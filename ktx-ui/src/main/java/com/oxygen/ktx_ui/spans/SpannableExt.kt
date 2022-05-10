package com.oxygen.ktx_ui.spans

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

inline fun SpannableStringBuilder.setClickSpanWithColor(
    spanEntry: String,
    color: Int,
    shouldUnderline: Boolean = false,
    crossinline onClick: () -> Unit,
) {
    val start = indexOf(spanEntry)
    val end = start + spanEntry.length

    val clickSpan = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = shouldUnderline
        }

        override fun onClick(widget: View) {
            onClick.invoke()
        }
    }

    setSpan(
        clickSpan,
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}
