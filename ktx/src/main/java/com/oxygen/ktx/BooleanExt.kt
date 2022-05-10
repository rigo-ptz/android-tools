package com.oxygen.ktx

fun Boolean?.orFalse(): Boolean = this ?: false

fun Boolean.toInt(): Int = if (this) 1 else 0
