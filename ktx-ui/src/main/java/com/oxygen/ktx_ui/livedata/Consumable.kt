package com.oxygen.ktx_ui.livedata

data class Consumable<out T>(private val content: T) {

    private var hasBeenConsumed = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotConsumed(): T? {
        return if (hasBeenConsumed) {
            null
        } else {
            hasBeenConsumed = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content

    /**
     * Consume the content if it is not consumed before.
     */
    fun consume(onConsume: T.() -> Unit) {
        getContentIfNotConsumed()?.apply(onConsume)
    }

}

fun <T> T.toConsumable(): Consumable<T> =
    Consumable(this)
