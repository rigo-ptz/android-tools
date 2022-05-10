package com.oxygen.ktx_ui.views

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.widget.ViewAnimator
import androidx.appcompat.widget.SearchView
import com.oxygen.ktx_ui.screen.dpToPx

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
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

fun ViewAnimator.displayChild(child: View) {
    indexOfChild(child)
        .takeIf { it != displayedChild }
        ?.let { displayedChild = it }
}

/**
 * Set onClickListener on list of view's
 */
fun List<View>.setOnClickListener(block: () -> Unit) {
    this.forEach { view ->
        view.setOnClickListener {
            block.invoke()
        }
    }
}

fun View.addRippleBorderless() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

/**
 * Set onClickListener using [DebounceOnClickListener]
 */
fun View.setDebounceClickListener(block: () -> Unit) {
    this.setOnClickListener(object : DebounceOnClickListener() {
        override fun onDebounceClick(v: View) {
            block()
        }
    })
}


/**
 * Sets the margins of a view. Provided values must be in density pixels.
 */
fun View.setMarginsDp(
    leftMargin: Int? = null,
    topMargin: Int? = null,
    rightMargin: Int? = null,
    bottomMargin: Int? = null
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(
            leftMargin?.dpToPx()?.toInt() ?: this.leftMargin,
            topMargin?.dpToPx()?.toInt() ?: this.topMargin,
            rightMargin?.dpToPx()?.toInt() ?: this.rightMargin,
            bottomMargin?.dpToPx()?.toInt() ?: this.bottomMargin,
        )
    }
}

fun ViewTreeObserver.doOnPreDraw(block: () -> Unit) {
    addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            block.invoke()
            removeOnPreDrawListener(this)
            return true
        }
    })
}

fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding, InitialMargin) -> Unit) {
    val initialPadding =
        InitialPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom)

    val marginParams = this.layoutParams as ViewGroup.MarginLayoutParams

    val initialMargin = InitialMargin(
        marginParams.leftMargin, marginParams.topMargin,
        marginParams.rightMargin, marginParams.bottomMargin
    )

    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding, initialMargin)
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

data class Dimensions(
    val left: Int, val top: Int,
    val right: Int, val bottom: Int,
)

typealias InitialPadding = Dimensions
typealias InitialMargin = Dimensions

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
