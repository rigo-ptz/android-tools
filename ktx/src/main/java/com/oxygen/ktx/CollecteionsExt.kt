package com.oxygen.ktx

fun <T> List<T>.containsAny(vararg items: T): Boolean =
    items.any { this.contains(it) }

fun <T> List<T>.containsAny(items: Collection<T>): Boolean =
    items.any { this.contains(it) }

fun <T : Any?> T.isOneOf(vararg items: T) =
    items.contains(this)

fun <T> List<T>.takeIfNotEmpty(): List<T>? =
    takeIf { isNotEmpty() }

inline fun <T> Iterable<T>.findFirstIndexed(predicate: (T) -> Boolean): Pair<Int, T>? {
    for ((index, item) in this.withIndex()) {
        if (predicate(item)) {
            return index to item
        }
    }
    return null
}
