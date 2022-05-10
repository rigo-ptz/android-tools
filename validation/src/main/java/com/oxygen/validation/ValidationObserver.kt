package com.oxygen.validation

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.oxygen.validation.utils.combineLatest

class ValidationObserver(
    private var lifecycleOwner: LifecycleOwner?,
    private val validators: List<LiveData<Boolean>>,
    private val skipFirstEvents: Boolean = false,
    private val fireIfAtLeastOneIsValid: Boolean = false,
    callback: ((Boolean) -> Unit)?
) : DefaultLifecycleObserver {

    private var lastResult: LiveData<Boolean>? = null

    private val observer = object : Observer<Boolean> {
        override fun onChanged(t: Boolean?) {
            callback?.invoke(t ?: return)
        }
    }

    init {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        lastResult = validators.combineLatest(skipFirstEvents) {
            if (fireIfAtLeastOneIsValid) it.contains(true) else !it.contains(false)
        }
        lastResult?.observe(owner, observer)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastResult?.removeObserver(observer)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycleOwner?.lifecycle?.removeObserver(this)
        lifecycleOwner = null
        lastResult = null
    }

    fun getLastResult() = lastResult?.value ?: false

}
