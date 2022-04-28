package com.oxygen.ktx_ui.databinding

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.oxygen.ktx_ui.resources.HtmlStringResource
import com.oxygen.ktx_ui.resources.ImageResource
import com.oxygen.ktx_ui.resources.StringResource

@BindingAdapter(value = ["textRes"])
fun setTextViewStringResource(
  view: TextView,
  stringResource: StringResource?
) {
  if (stringResource is HtmlStringResource) {
    val htmlText = stringResource.getMessage(view.context).toString()
    view.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
      HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    view.movementMethod = LinkMovementMethod.getInstance()
  } else {
    stringResource?.apply {
      view.text = getMessage(view.context)
    }
  }
}

@BindingAdapter(value = ["drawableTintColor"])
fun setDrawableTintColor(view: TextView, @ColorInt color: Int) =
  view.compoundDrawables.forEach {
    it?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
  }

@BindingAdapter(
  value = ["drawableStartRes", "drawableTopRes", "drawableEndRes", "drawableBottomRes", "drawableTintRes"],
  requireAll = false
)
fun setTextViewDrawableResource(
  view: TextView,
  drawableStartRes: ImageResource?,
  drawableTopRes: ImageResource?,
  drawableEndRes: ImageResource?,
  drawableBottomRes: ImageResource?,
  drawableTintRes: ColorResource?
) {
  with(view) {
    val drawableStart = drawableStartRes?.getDrawable(context)
    val drawableTop = drawableTopRes?.getDrawable(context)
    val drawableEnd = drawableEndRes?.getDrawable(context)
    val drawableBottom = drawableBottomRes?.getDrawable(context)

    listOfNotNull(drawableStart, drawableTop, drawableEnd, drawableBottom).forEach {
      drawableTintRes?.apply {
        DrawableCompat.setTintList(it, ColorStateList.valueOf(getColor(view.context)))
      }
    }

    setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom)
  }
}

@BindingAdapter("imageResource")
fun setImageResource(view: ImageView, imageResource: ImageResource?) {
  imageResource?.loadTo(view)
}

@BindingAdapter(value = ["app:tint"])
fun setImageTint(imageView: ImageView, @ColorInt resId: Int?) {
  resId?.apply {
    imageView.imageTintList = ColorStateList.valueOf(this)
  }
}

@BindingAdapter("bgColorRes")
fun setBackgroundColorResource(view: View, colorResource: ColorResource?) {
  colorResource?.apply {
    when (view) {
      is ImageView -> view.imageTintList = ColorStateList.valueOf(getColor(view.context))
      is TextView -> view.background = ColorDrawable(getColor(view.context))
      else -> view.backgroundTintList = ColorStateList.valueOf(getColor(view.context))
    }
  }
}

@BindingAdapter(value = ["leftColor", "rightColor"], requireAll = true)
fun setLinearGradientBg(view: View, leftColor: ColorResource?, rightColor: ColorResource?) {
  safeLet(leftColor, rightColor) { left, right ->
    view.background = GradientDrawable(
      GradientDrawable.Orientation.LEFT_RIGHT,
      intArrayOf(left.getColor(view.context), right.getColor(view.context))
    ).apply {
      gradientType = GradientDrawable.LINEAR_GRADIENT
      shape = GradientDrawable.RECTANGLE
      setSize(view.width, view.height)
    }
  }
}

/**
 * Invokes the action when [EditText] gets focused
 */
@BindingAdapter("onHasFocus")
fun onHasFocus(text: EditText, action: () -> Unit) {
  text.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
    if (hasFocus)
      action()
  }
}

/**
 * Invokes the listener every time the text is changed
 */
@BindingAdapter("onTextChanged")
fun onTextChanged(editText: EditText, listener: OnTextChanged) {
  editText.addTextChangedListener(object : TextWatcher {
    override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
      listener.onTextChanged(charSequence)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
  })
}

interface OnTextChanged {
  fun onTextChanged(text: CharSequence)
}

@BindingAdapter(value = ["model"])
fun setupRecyclerView(
  recyclerView: RecyclerView,
  recyclerViewBindingModel: RecyclerViewBindingModel?
) {
  recyclerViewBindingModel?.also { model ->
    val bindingAdapter = if (recyclerView.adapter is DataBindingAdapter) {
      recyclerView.adapter as DataBindingAdapter
    } else {
      DataBindingAdapter()
    }

    with(recyclerView) {
      layoutManager = model.layoutManager

      if (recyclerView.itemDecorationCount > 0) {
        for (index in 0 until recyclerView.itemDecorationCount) {
          recyclerView.removeItemDecorationAt(0)
        }
      }
      model.itemDecoration?.run { recyclerView.addItemDecoration(this) }

      suppressLayout(model.suppressLayout)

      adapter = bindingAdapter
    }

    with(bindingAdapter) {
      animateOnChange = model.animateOnChange
      update(model.dataBindingItems)
    }
  }
}

@BindingAdapter(value = ["playTexts"])
fun playTexts(
  textSwitcher: TextSwitcher,
  texts: List<StringResource>?
) {
  texts?.forEachIndexed { index, stringResource ->
    val delayed = 2000L * index
    textSwitcher.postDelayed({
      textSwitcher.setText(stringResource.getMessage(textSwitcher.context))
    }, delayed)
  }
}

@BindingAdapter(value = ["onEditorAction"])
fun onEditorAction(view: EditText, action: (() -> Unit)?) {
  if (action == null) {
    view.setOnEditorActionListener(null)
  } else {
    view.setOnEditorActionListener { _, actionId, event ->
      val imeAction = when (actionId) {
        EditorInfo.IME_ACTION_DONE,
        EditorInfo.IME_ACTION_SEND,
        EditorInfo.IME_ACTION_GO -> true
        else -> false
      }

      val keyEvent = event?.keyCode == KeyEvent.KEYCODE_ENTER
          && event.action == KeyEvent.ACTION_DOWN

      if (imeAction or keyEvent) true.also { action() }
      else false
    }
  }
}

@BindingAdapter("cardBackground")
fun setCardBackgroundColor(view: MaterialCardView, @ColorRes color: Int) {
  view.setCardBackgroundColor(ContextCompat.getColor(view.context, color))
}

@BindingAdapter(value = ["isChecked"])
fun setIsChecked(view: View, isChecked: Boolean) {
  (view as? Checkable)?.isChecked = isChecked
}

@BindingAdapter("layoutMarginBottom")
fun setLayoutMarginBottom(view: View, dimen: Float) {
  val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
  layoutParams.bottomMargin = dimen.toInt()
  view.layoutParams = layoutParams
}
