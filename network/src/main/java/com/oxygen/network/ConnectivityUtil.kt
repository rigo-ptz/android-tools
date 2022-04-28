package com.oxygen.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * @author Iamushev Igor
 * @since  27.4.2022
 */
class ConnectivityUtil(
    private var context: Context?
) : LiveData<Boolean>() {

    private var connectivityManager = context!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            postCurrentStatus()
        }

        override fun onLost(network: Network) {
            postCurrentStatus()
        }
    }

    override fun onActive() {
        super.onActive()
        postCurrentStatus()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        context = null
    }

    private fun postCurrentStatus() {
        postValue(hasActiveNetworks())
    }

    private fun hasActiveNetworks(): Boolean =
        connectivityManager.allNetworks
            .map { connectivityManager.getNetworkInfo(it)?.isConnectedOrConnecting ?: false }
            .contains(true)

}
