package com.moufee.purduemenus.util

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

class NetworkAvailabilityListener(context: Context, lifecycle: Lifecycle, onActiveCallback: () -> Unit) : DefaultLifecycleObserver {
    private val connectivityManager: ConnectivityManager = context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Timber.d("Running")
            onActiveCallback()
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onStop(owner: LifecycleOwner) {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}