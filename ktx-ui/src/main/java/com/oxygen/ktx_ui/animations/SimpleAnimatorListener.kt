package com.oxygen.ktx_ui.animations

import android.animation.Animator

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
class SimpleAnimatorListener(
    private val onStart: ((animator: Animator?) -> Unit)? = null,
    private val onEnd: ((animator: Animator?) -> Unit)? = null,
    private val onRepeat: ((animator: Animator?) -> Unit)? = null,
    private val onCancel: ((animator: Animator?) -> Unit)? = null
) : Animator.AnimatorListener {

    override fun onAnimationRepeat(animator: Animator?) {
        onRepeat?.invoke(animator)
    }

    override fun onAnimationEnd(animator: Animator?) {
        onEnd?.invoke(animator)
    }

    override fun onAnimationCancel(animator: Animator?) {
        onCancel?.invoke(animator)
    }

    override fun onAnimationStart(animator: Animator?) {
        onStart?.invoke(animator)
    }
}
