package com.oxygen.ktx_ui.views.fragment

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import kotlin.properties.ReadOnlyProperty

/**
 * @author Iamushev Igor
 * @since  24.4.2022
 */
inline fun <reified T> extra(
    key: String,
    default: T?,
    removeFromArguments: Boolean = false
): ReadOnlyProperty<Fragment, T?> =
    ReadOnlyProperty { thisRef, _ ->
        val arguments = thisRef.arguments
        val data = (arguments?.get(key) as? T)
        if (data != null && removeFromArguments) arguments.remove(key)
        data ?: default
    }

inline fun <reified T> extraNonNull(
    key: String,
    removeFromArguments: Boolean = false
): ReadOnlyProperty<Fragment, T> =
    ReadOnlyProperty { thisRef, _ ->
        val arguments = thisRef.arguments
        val data = arguments?.get(key) as T
        if (removeFromArguments) arguments?.remove(key)
        data!!
    }

inline fun <reified T : View> Fragment.find(@IdRes id: Int): T = view?.findViewById(id) as T

fun <T> Fragment.getNavigationResult(key: String, removeFromArguments: Boolean = false) : T? {
    val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle ?: return null
    val result = savedStateHandle.get<T>(key)
    if (removeFromArguments)
        savedStateHandle.remove<T>(key)
    return result
}

fun <T> Fragment.getNavigationResultLiveData(key: String) =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key) as? LiveData<T>

fun <T> Fragment.observeNavigationResult(
    key: String,
    removeFromArguments: Boolean,
    consumer: (T) -> Boolean
) {
    val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle ?: return

    savedStateHandle.getLiveData<T>(key).observe(viewLifecycleOwner) {
            val isConsumed = consumer.invoke(it)

            if (removeFromArguments && isConsumed) {
                savedStateHandle.remove<T>(key)
            }
        }
}

fun <T> Fragment.setNavigationResult(key: String, result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Fragment.isFragmentVisible(): Boolean = isAdded && isVisible && activity?.isFinishing != true

fun Fragment.initBackPressedListener(backPressedCallback: OnBackPressedCallback) {
    backPressedCallback.isEnabled = false
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
}
