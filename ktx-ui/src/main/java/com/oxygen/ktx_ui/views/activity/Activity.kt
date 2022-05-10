package com.oxygen.ktx_ui.views.activity

import android.app.Activity
import android.content.Intent
import kotlin.properties.ReadOnlyProperty

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
inline fun <reified T> extra(
    key: String,
    default: T?,
    removeFromIntent: Boolean = false
): ReadOnlyProperty<Activity, T?> =
    ReadOnlyProperty { thisRef, _ ->
        val intent = thisRef.intent
        val data = (intent?.extras?.get(key) as? T)
        if (data != null && removeFromIntent) intent.removeExtra(key)
        data ?: default
    }

inline fun <reified T> extraNonNull(
    key: String,
    removeFromIntent: Boolean = false
): ReadOnlyProperty<Activity, T> =
    ReadOnlyProperty { thisRef, _ ->
        val intent = thisRef.intent
        val data = intent?.extras?.get(key) as T
        if (removeFromIntent) intent.removeExtra(key)
        data
    }

fun Intent.clearStack(): Intent =
    this.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

fun Activity?.setBrightnessLevel(level: Float) {
    if (this == null) return
    window?.attributes = window?.attributes?.apply {
        screenBrightness = level
    }
}
