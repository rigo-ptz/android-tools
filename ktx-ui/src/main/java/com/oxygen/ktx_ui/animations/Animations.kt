package com.oxygen.ktx_ui.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.oxygen.ktx_ui.context.color
import com.oxygen.ktx_ui.dimensions.dpToPx

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
fun TextView.expandToLines(lineCount: Int) {
    ObjectAnimator.ofInt(
        this,
        "maxLines",
        lineCount
    ).apply {
        duration = 300
        interpolator = AccelerateInterpolator()
    }.start()
}

fun TextView.collapseToLines(lineCount: Int) {
    ObjectAnimator.ofInt(
        this,
        "maxLines",
        lineCount
    ).apply {
        duration = 300
        interpolator = AccelerateInterpolator()
    }.start()
}

fun Animation.setAnimationListener(
    onStart: ((animation: Animation?) -> Unit)? = null,
    onEnd: ((animation: Animation?) -> Unit)? = null,
    onRepeat: ((animation: Animation?) -> Unit)? = null
) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animation?) {
            onRepeat?.invoke(animation)
        }
    })
}

fun Animator.addListener(
    onStart: ((animator: Animator?) -> Unit)? = null,
    onEnd: ((animator: Animator?) -> Unit)? = null,
    onRepeat: ((animator: Animator?) -> Unit)? = null,
    onCancel: ((animator: Animator?) -> Unit)? = null
) = this.addListener(
    SimpleAnimatorListener(
        onStart = onStart,
        onEnd = onEnd,
        onRepeat = onRepeat,
        onCancel = onCancel
    )
)

fun ViewPropertyAnimator.setListener(
    onStart: ((animator: Animator?) -> Unit)? = null,
    onEnd: ((animator: Animator?) -> Unit)? = null,
    onRepeat: ((animator: Animator?) -> Unit)? = null,
    onCancel: ((animator: Animator?) -> Unit)? = null
): ViewPropertyAnimator =
    this.setListener(
        SimpleAnimatorListener(
            onStart = onStart,
            onEnd = onEnd,
            onRepeat = onRepeat,
            onCancel = onCancel
        )
    )

fun Transition.addListener(
    onTransitionResume: ((p0: Transition?) -> Unit)? = null,
    onTransitionPause: ((p0: Transition?) -> Unit)? = null,
    onTransitionCancel: ((p0: Transition?) -> Unit)? = null,
    onTransitionStart: ((p0: Transition?) -> Unit)? = null,
    onTransitionEnd: ((p0: Transition?) -> Unit)? = null
) {
    this.addListener(object : Transition.TransitionListener {
        override fun onTransitionResume(transition: Transition?) {
            onTransitionResume?.invoke(transition)
        }

        override fun onTransitionPause(transition: Transition?) {
            onTransitionPause?.invoke(transition)
        }

        override fun onTransitionCancel(transition: Transition?) {
            onTransitionCancel?.invoke(transition)
        }

        override fun onTransitionStart(transition: Transition?) {
            onTransitionStart?.invoke(transition)
        }

        override fun onTransitionEnd(transition: Transition?) {
            onTransitionEnd?.invoke(transition)
        }
    })
}

fun ViewGroup.measureWrapContentHeight(): Int {
    this.measure(
        View.MeasureSpec
            .makeMeasureSpec((this.parent as View).measuredWidth, View.MeasureSpec.EXACTLY),
        View.MeasureSpec
            .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    return measuredHeight
}

fun ViewGroup.collapse() {
    val actualHeight = this.height
    val anim = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1f) {
                this@collapse.layoutParams.height = 0
                this@collapse.isVisible = false
            } else {
                this@collapse.layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }
    }
    val speed = 1.dpToPx() // 1 dp in 1ms
    anim.duration = (actualHeight / speed).toLong()
    this.clearAnimation()
    this.startAnimation(anim)
}

fun ViewGroup.expandToWrapContent() {
    val targetHeight = this.measureWrapContentHeight()
    isVisible = true
    val anim = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            this@expandToWrapContent.layoutParams.height =
                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
            this@expandToWrapContent.requestLayout()
        }
    }
    val speed = 1.dpToPx() // 1 dp in 1ms
    anim.duration = (targetHeight / speed).toLong()
    this.clearAnimation()
    this.startAnimation(anim)
}

fun View.slideInFromBottom(duration: Long = 400L) {
    clearAnimation()
    translationY = height.toFloat()
    animate()
        .translationY(0f)
        .setInterpolator(FastOutSlowInInterpolator())
        .setDuration(duration)
        .setListener(onStart = { isVisible = true })
        .start()
}

fun View.slideOutToBottom(duration: Long = 400L) {
    clearAnimation()
    translationY = 0f
    animate()
        .translationY(height.toFloat())
        .setInterpolator(FastOutSlowInInterpolator())
        .setDuration(duration)
        .setListener(onEnd = { isVisible = false })
        .start()
}

fun View.slideOutToTop(duration: Long = 400L) {
    clearAnimation()
    translationY = 0f
    animate()
        .translationY(-height.toFloat())
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .setListener(onEnd = { isVisible = false })
        .start()
}

fun View.slideInFromTop(duration: Long = 400L) {
    clearAnimation()
    translationY = -height.toFloat()
    animate()
        .translationY(0f)
        .setDuration(duration)
        .setInterpolator(FastOutSlowInInterpolator())
        .setListener(onStart = { isVisible = true })
        .start()
}

fun View.fadeIn(duration: Long = 400L) {
    clearAnimation()
    animate()
        .alpha(1f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(duration)
        .setListener(onStart = { isVisible = true })
        .start()
}

fun View.fadeOut(duration: Long = 400L) {
    clearAnimation()
    animate()
        .alpha(0f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(duration)
        .setListener(onEnd = { isVisible = false })
        .start()
}

fun View.animateHover(dY: Float, duration: Long) {
    clearAnimation()
    ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("translationY", -dY),
        PropertyValuesHolder.ofFloat("translationY", dY)
    ).apply {
        this.duration = duration
        interpolator = AccelerateDecelerateInterpolator()
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
        start()
    }
}

fun View.animateBackgroundColor(
    @ColorRes startColorRes: Int,
    @ColorRes endColorRes: Int,
    duration: Long
) {
    clearAnimation()
    val colorStart = context.color(startColorRes)
    val colorEnd = context.color(endColorRes)

    ValueAnimator.ofObject(ArgbEvaluator(), colorStart, colorEnd, colorStart).apply {
        this.duration = duration
        interpolator = LinearInterpolator()

        addUpdateListener { animator ->
            this@animateBackgroundColor.setBackgroundColor(animator.animatedValue as Int)
        }

        start()
    }
}

fun View.circularReveal(
    x: Int,
    y: Int,
    endRadius: Int,
    onAnimationEnd: (() -> Unit)? = null
) {
    isVisible = true

    CircularRevealAnimationUtil.buildRevealAnimator(
        this,
        x,
        y,
        0,
        endRadius,
        SimpleAnimatorListener(
            onEnd = {
                onAnimationEnd?.invoke()
            }
        )
    ).start()
}

fun View.circularHide(
    x: Int,
    y: Int,
    startRadius: Int,
    endRadius: Int = 0,
    onAnimationEnd: (() -> Unit)? = null
) {
    CircularRevealAnimationUtil.buildRevealAnimator(
        this,
        x,
        y,
        startRadius,
        endRadius,
        SimpleAnimatorListener(
            onEnd = {
                isVisible = false
                onAnimationEnd?.invoke()
            }
        )
    ).start()
}

fun View.animateShrink(duration: Long = 400L) {
    clearAnimation()
    animate()
        .scaleX(0f)
        .scaleY(0f)
        .setInterpolator(FastOutSlowInInterpolator())
        .setDuration(duration)
        .setListener(null)
        .start()
}

fun View.animateUnshrink(duration: Long = 400L) {
    clearAnimation()
    animate()
        .scaleX(1f)
        .scaleY(1f)
        .setInterpolator(BounceInterpolator())
        .setDuration(duration)
        .setListener(null)
        .start()
}

fun TextView.animateTextColorChange(
    @ColorRes startColorRes: Int,
    @ColorRes endColorRes: Int,
    duration: Long = 800L
) {
    val colorFrom = ContextCompat.getColor(context, startColorRes)
    val colorTo = ContextCompat.getColor(context, endColorRes)

    ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
        this.duration = duration
        addUpdateListener { animator -> setTextColor(animator.animatedValue as Int) }
        start()
    }
}

/**
 * Generic method to add spring animation.
 */
fun View.spring(
    property: DynamicAnimation.ViewProperty,
    damping: Float = SpringForce.DAMPING_RATIO_NO_BOUNCY,
    stiffness: Float = 500f
): SpringAnimation =
    SpringAnimation(
        this,
        property
    ).apply {
        spring = SpringForce().apply {
            this.dampingRatio = damping
            this.stiffness = stiffness
        }
    }
