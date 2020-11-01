package com.moufee.purduemenus.util

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class NetworkAvailabilityListener(context: Context, lifecycle: Lifecycle, onActiveCallback: () -> Unit) : LifecycleObserver {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun start() {
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}