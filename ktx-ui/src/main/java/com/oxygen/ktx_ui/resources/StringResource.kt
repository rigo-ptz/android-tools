package com.oxygen.ktx_ui.resources

import android.content.Context
import android.graphics.Typeface
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.oxygen.ktx_ui.spans.getTypefaceSpan
import java.io.Serializable

interface StringResource : Serializable {

  fun getMessage(context: Context): CharSequence

  @Composable
  fun getMessage(): String

}

data class HtmlStringResource(private val stringResource: StringResource) : StringResource {

  override fun getMessage(context: Context): CharSequence =
    stringResource.getMessage(context).replace(Regex("&lt;"), "<")
      .replace(Regex("&gt;"), ">")
      .replace(Regex("‒"), "-")
      .replace("\\n", "<br>")

  @Composable
  override fun getMessage(): String = TODO("To implement in the future")

}


@Suppress("MemberVisibilityCanBePrivate")
data class StaticStringResource(val msg: String?) : StringResource {

  override fun getMessage(context: Context): CharSequence = msg.orEmpty()

  @Composable
  override fun getMessage(): String = msg.orEmpty()

}

class EmptyStringResource : StringResource {

  override fun getMessage(context: Context): CharSequence = ""

  @Composable
  override fun getMessage(): String = ""

}

data class AndroidStringResource(
  @StringRes val stringRes: Int,
  val args: List<Any?> = emptyList()
) : StringResource {

  constructor(@StringRes stringRes: Int, vararg args: Any?) : this(stringRes, args.toList())

  override fun getMessage(context: Context): CharSequence {
    return args.map {
      when (it) {
        is StringResource -> it.getMessage(context)
        else -> it
      }
    }.let {
      if (it.isEmpty()) context.getString(stringRes)
      else context.getString(stringRes, *it.toTypedArray())
    }
  }

  @Composable
  override fun getMessage(): String =
    stringResource(
      stringRes,
      *args.map {
        when (it) {
          is StringResource -> it.getMessage()
          else -> it!!
        }
      }.toTypedArray()
    )

}

data class ColorStringResource(
  val input: StringResource,
  val stringToColor: StringResource?,
  @ColorRes val color: Int,
) : StringResource {

  override fun getMessage(context: Context): CharSequence {
    val message = input.getMessage(context)
    val stringToColor = stringToColor?.getMessage(context)?.toString()

    return SpannableStringBuilder(message).apply {
      val startIndex = if (stringToColor == null) 0 else message.indexOf(stringToColor)
      if (startIndex == -1) return@apply
      val endIndex =
        if (stringToColor == null) message.length else startIndex + stringToColor.length

      setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
  }

  @Composable
  override fun getMessage(): String = TODO("To implement in the future")

}

/**
 * Sets bold style on [args]. In case [args] are not found in [stringResource] then the style is not
 * applied.
 */
data class BoldArgumentsStringResource(
  val stringRes: StringResource,
  val args: List<StringResource> = emptyList(),
) : StringResource {

  constructor(stringRes: StringResource, vararg args: StringResource) : this(
    stringRes,
    args.toList()
  )

  override fun getMessage(context: Context): CharSequence =
    SpannableString(stringRes.getMessage(context)).apply {
      args.forEach { argResource ->
        argResource.getMessage(context).toString().let { arg ->
          toString().indexOf(arg).takeIf { it != -1 }?.let { start ->
            setSpan(
              StyleSpan(Typeface.BOLD),
              start,
              start + arg.length,
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
          }
        }
      }
    }

  @Composable
  override fun getMessage(): String = TODO("To implement in the future")
}

data class QuantityStringResource(
  @PluralsRes val pluralRes: Int,
  val count: Int,
  val args: List<Any?> = emptyList()
) : StringResource {

  constructor(
    @PluralsRes pluralRes: Int,
    count: Int,
    vararg args: Any?
  ) : this(pluralRes, count, args.toList())

  override fun getMessage(context: Context): CharSequence =
    context.resources.getQuantityString(pluralRes, count, *args.toTypedArray())

  @Composable
  override fun getMessage(): String = TODO("To implement in the future")

}

/**
 * Applies font to [args]. In case [args] are not found in [stringResource] then the style is not
 * applied.
 */
data class FontStringResource(
  val stringRes: StringResource,
  @FontRes val fontResId: Int,
  val args: List<StringResource> = emptyList(),
) : StringResource {

  constructor(
    stringRes: StringResource,
    @FontRes fontResId: Int,
    vararg args: StringResource
  ) : this(
    stringRes,
    fontResId,
    args.toList()
  )

  override fun getMessage(context: Context): CharSequence =
    SpannableString(stringRes.getMessage(context)).apply {
      args.forEach { argResource ->
        argResource.getMessage(context).toString().let { arg ->
          toString().indexOf(arg).takeIf { it != -1 }?.let { start ->
            setSpan(
              context.getFont(fontResId).getTypefaceSpan(),
              start,
              start + arg.length,
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
          }
        }
      }
    }

  @Composable
  override fun getMessage(): String = TODO("To implement in the future")
}

