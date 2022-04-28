package com.oxygen.ktx_ui.views

import androidx.appcompat.app.AppCompatActivity

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
inline fun <reified T> AppCompatActivity.extra(
    key: String,
    default: T?,
    removeFromIntent: Boolean = false
): Lazy<T?> = lazy {
    val data = intent?.extras?.get(key) as? T
    if (data != null && removeFromIntent) intent.removeExtra(key)
    return@lazy data ?: default
}

inline fun <reified T> AppCompatActivity.extraNonNull(
    key: String,
    removeFromIntent: Boolean = false
): Lazy<T> = lazy {
    val data = intent.extras!!.get(key) as T
    if (removeFromIntent) intent.removeExtra(key)
    return@lazy data
}
