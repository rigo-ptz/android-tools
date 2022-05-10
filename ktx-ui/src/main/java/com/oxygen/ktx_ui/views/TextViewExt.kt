package com.oxygen.ktx_ui.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import com.oxygen.ktx_ui.context.color

/**
 * Shortcut for setting a keyboard done button listener
 */
fun EditText.setKeyboardActionDoneListener(block: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            block.invoke()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun TextView.textOrNull(): String? = text.toString().ifEmpty { null }

fun TextView.setTextAppearanceCompat(context: Context, @StyleRes resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        @Suppress("DEPRECATION")
        setTextAppearance(context, resId)
    }
}

fun TextView.setDrawableTint(@ColorRes color: Int) {
    val colorFilter = PorterDuffColorFilter(context.color(color), PorterDuff.Mode.SRC_IN)
    compoundDrawables.filterNotNull().forEach { it.colorFilter = colorFilter }
}

fun TextView.setHtmlText(
    html: String?,
    imageGetter: Html.ImageGetter? = null,
    tagHandler: Html.TagHandler? = null
) {
    this.movementMethod = LinkMovementMethod.getInstance()

    @Suppress("DEPRECATION")
    this.text = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, imageGetter, tagHandler)
        else -> Html.fromHtml(html, imageGetter, tagHandler)
    }
}
