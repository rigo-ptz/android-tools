package com.oxygen.ktx_ui.resources

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
/**
 * Get String by String resource identifier name,
 * otherwise returns null if String identifier is not available
 */
fun Context.getStringByName(identifierName: String, vararg formatArgs: Any?): String? {
    val resId = resources.getIdentifier(
        identifierName.lowercase(Locale.ENGLISH),
        "string",
        packageName
    )
    return if (resId == 0) null else getString(resId, *formatArgs)
}

fun Context.getPluralByName(identifierName: String, quantity: Int): String? {
    val resId = resources.getIdentifier(
        identifierName.lowercase(Locale.ENGLISH),
        "plurals",
        packageName
    )
    return if (resId == 0) null else resources.getQuantityString(resId, quantity, quantity)
}

/**
 * Returns [Resources] enforced with [Configuration] of a desired [Locale]
 */
fun Context.getLocalizedResources(
    desiredLocale: Locale
): Resources {
    var conf = resources.configuration
    // clone configuration to restrict locale to desired one
    conf = Configuration(conf)
    conf.setLocale(desiredLocale)
    val localizedContext = createConfigurationContext(conf)
    return localizedContext.resources
}
