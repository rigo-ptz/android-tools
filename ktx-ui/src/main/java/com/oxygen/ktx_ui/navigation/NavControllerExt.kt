package com.oxygen.ktx_ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator

fun NavController.navigateSafe(
    destination: NavDirections,
    extras: FragmentNavigator.Extras? = null
) {
    val action = currentDestination?.getAction(destination.actionId)
        ?: graph.getAction(destination.actionId)
    if (action != null && currentDestination?.id != action.destinationId) {
        extras?.run {
            navigate(destination, this)
        } ?: navigate(destination)
    }
}

fun NavController.navigateSafe(
    destination: NavDirections,
    navOptions: NavOptions? = null
) {
    val action = currentDestination?.getAction(destination.actionId)
        ?: graph.getAction(destination.actionId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(destination, navOptions)
    }
}
