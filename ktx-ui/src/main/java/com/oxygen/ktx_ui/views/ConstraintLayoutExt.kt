package com.oxygen.ktx_ui.views

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

fun ConstraintLayout.updateConstraints(block: (ConstraintSet) -> Unit) {
    val set = ConstraintSet()
    set.clone(this)
    block.invoke(set)
    set.applyTo(this)
}

fun MotionLayout.doOnTransitionEvents(
    onTransitionTrigger: ((motionLayout: MotionLayout, triggerId: Int, positive: Boolean, progress: Float) -> Unit)? = null,
    onTransitionStarted: ((motionLayout: MotionLayout, startId: Int, endId: Int) -> Unit)? = null,
    onTransitionChange: ((motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) -> Unit)? = null,
    onTransitionCompleted: ((motionLayout: MotionLayout, currentId: Int) -> Unit)? = null
) {
    setTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionTrigger(
            motionLayout: MotionLayout,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        ) {
            onTransitionTrigger?.invoke(motionLayout, triggerId, positive, progress)
        }

        override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
            onTransitionStarted?.invoke(motionLayout, startId, endId)
        }

        override fun onTransitionChange(
            motionLayout: MotionLayout,
            startId: Int,
            endId: Int,
            progress: Float
        ) {
            onTransitionChange?.invoke(motionLayout, startId, endId, progress)
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
            onTransitionCompleted?.invoke(motionLayout, currentId)
        }
    })
}
