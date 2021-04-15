package com.moufee.purduemenus.api

import android.content.SharedPreferences
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

// todo: unnecessary generic?
abstract class AuthenticatedAPITask<T>(private val mSharedPreferences: SharedPreferences) : Runnable {
    override fun run() {
        if (!isLoggedIn) return
        try {
            val initialCall = call
            val response = initialCall.execute()
            Timber.d("run: %s", response)
            if (response.isSuccessful) {
                onSuccess(response)
            } else {
                //logout or something
                mSharedPreferences.edit().putBoolean("logged_in", false).apply()
                Timber.d("unsuccessful response %s", response.message())
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception while running task")
        }
    }

    abstract val call: Call<T>
    abstract fun onSuccess(response: Response<T>)
    private val isLoggedIn: Boolean
        get() = mSharedPreferences.getBoolean("logged_in", false)

    companion object {
        private const val TAG = "FavoriteTransaction"
    }
}