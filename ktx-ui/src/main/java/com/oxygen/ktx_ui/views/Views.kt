package com.oxygen.ktx_ui.views

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
fun TextView.textOrNull(): String? = text.toString().ifEmpty { null }

@Suppress("UNCHECKED_CAST")
fun <V : View> ViewGroup.forEachChild(function: V.() -> Unit) {
    for (i in 0 until childCount) {
        (getChildAt(i) as? V)?.apply { function(this) }
    }
}

fun SearchView.onQuery(
    onQueryTextSubmit: ((String?) -> Unit)? = null,
    onQueryTextChange: ((String) -> Unit)? = null
) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean{
            onQueryTextSubmit?.invoke(query)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            onQueryTextChange?.invoke(newText.orEmpty())
            return true
        }
    })
}

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
