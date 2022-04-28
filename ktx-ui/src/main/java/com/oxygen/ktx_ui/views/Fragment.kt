package com.oxygen.ktx_ui.views

import androidx.fragment.app.Fragment

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
inline fun <reified T> Fragment.extra(
    key: String,
    default: T? = null,
    removeFromArguments: Boolean = false
): Lazy<T?> = lazy {
    val data = arguments?.get(key) as? T
    if (data != null && removeFromArguments) arguments?.remove(key)
    return@lazy data ?: default
}

inline fun <reified T> Fragment.extraNonNull(
    key: String,
    default: T? = null,
    removeFromArguments: Boolean = false
): Lazy<T> = lazy {
    val data = arguments!!.get(key) as T
    if (removeFromArguments) arguments?.remove(key)
    return@lazy data
}
