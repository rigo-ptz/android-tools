package com.oxygen.ktx_ui.animations

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
object CircularRevealAnimationUtil {

    fun buildRevealAnimator(
        view: View,
        x: Int,
        y: Int,
        startRadius: Int,
        endRadius: Int,
        listener: Animator.AnimatorListener?
    ): Animator {
        val anim = ViewAnimationUtils
            .createCircularReveal(view, x, y, startRadius.toFloat(), endRadius.toFloat())
            .setDuration(
                view.context.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            )
        anim.interpolator = FastOutSlowInInterpolator()
        listener?.let { anim.addListener(it) }
        return anim
    }

}
