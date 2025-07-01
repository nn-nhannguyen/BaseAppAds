package com.app.base.mvvm.arch.extensions

import java.nio.charset.StandardCharsets
import java.util.*

fun String.tryToIntOrZero(): Int = toIntOrNull() ?: 0
fun String.tryToLongOrZero(): Long = toLongOrNull() ?: 0
fun String.tryToDoubleOrZero(): Double = toDoubleOrNull() ?: 0.0
fun String.orNull(): String? = if (isNotEmpty()) this else null
fun String.spaceFullWidth(): String = "　"
fun String.empty(): String = ""

/**
 * encode string by base64
 */
fun String.encodeToBase64(): String {
  if (isEmpty()) { return "" }
  return Base64.getEncoder().encodeToString(this.toByteArray(StandardCharsets.UTF_8))
}
