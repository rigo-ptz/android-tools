package com.oxygen.ktx

import java.util.Locale

infix fun String?.equalsIgnoreCase(other: String?): Boolean =
    this.equals(other, ignoreCase = true)

infix fun String?.containsIgnoreCase(other: String): Boolean =
    this?.lowercase(Locale.getDefault())?.contains(other.lowercase(Locale.getDefault())) ?: false

fun String?.isOneOfIgnoreCase(vararg strings: String): Boolean =
    strings.firstOrNull { this.equals(it, ignoreCase = true) } != null

fun String?.isOneOfIgnoreCase(strings: List<String>): Boolean =
    strings.firstOrNull { this.equals(it, ignoreCase = true) } != null

fun String?.containsAnyOf(vararg strings: String, ignoreCase: Boolean = true): Boolean =
    strings.any {
        this?.contains(it, ignoreCase) ?: false
    }

fun List<String>.containsAny(vararg strings: String, ignoreCase: Boolean = true): Boolean =
    any { stringToCheck -> strings.firstOrNull { stringToCheck.equals(it, ignoreCase) } != null }

fun List<String>.containsAny(strings: List<String>, ignoreCase: Boolean = true): Boolean =
    containsAny(*strings.toTypedArray(), ignoreCase)

/**
 * Shortcut for String?.startsWith(String?, ignoreCase = true)
 */
infix fun String?.startsWithIgnoreCase(other: String): Boolean =
    this != null && this.startsWith(other, ignoreCase = true)

/**
 * Capitalize first letter of every word in the sentence and don't set other letters to lower case
 */
fun String?.capitalizeWords(): String =
    this?.split(" ")
        ?.joinToString(separator = " ") { string ->
            string.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else
                    it.toString()
            }
        }.orEmpty()

fun joinNonNullTokens(separator: String, vararg tokens: String?): String =
    tokens.filterNotNull().joinToString(separator = separator)
