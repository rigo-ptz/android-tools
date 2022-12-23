package com.oxygen.ktx_ui.spans

import android.annotation.TargetApi
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.MetricAffectingSpan
import android.text.style.TypefaceSpan
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

fun Typeface.getTypefaceSpan(): MetricAffectingSpan {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        typefaceSpanCompatV28(this)
    else
        CustomTypefaceSpan(this)
}

@TargetApi(Build.VERSION_CODES.P)
private fun typefaceSpanCompatV28(typeface: Typeface) = TypefaceSpan(typeface)

private class CustomTypefaceSpan(private val typeface: Typeface?) : MetricAffectingSpan() {
    override fun updateDrawState(paint: TextPaint) {
        paint.typeface = typeface
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.typeface = typeface
    }
}
