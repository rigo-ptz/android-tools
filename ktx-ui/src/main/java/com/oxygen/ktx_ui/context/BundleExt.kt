package com.oxygen.ktx_ui.context

import android.os.Bundle

fun <T : Enum<T>> Bundle.putEnum(key: String, value: T) = putString(key, value.name)

inline fun <reified T : Enum<T>> Bundle.getEnum(key: String, default: T): T =
    getString(key)?.let { enumValueOf<T>(it) } ?: default

inline fun <reified T : Enum<T>> Bundle.getEnum(key: String): T? =
    getString(key)?.let { enumValueOf<T>(it) }

fun Bundle.toPrintableString(): String {
    return keySet().joinToString(prefix = "{", postfix = "}") { key ->
        val keyValueString = when (val keyValue = get(key)) {
            is Bundle -> keyValue.toPrintableString()
            is Array<*> -> keyValue.joinToString()
            is Iterable<*> -> keyValue.joinToString()
            else -> keyValue.toString()
        }

        "$key=$keyValueString"
    }
}
