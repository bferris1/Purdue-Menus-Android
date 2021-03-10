package com.moufee.purduemenus.repository

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moufee.purduemenus.util.AuthHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthenticationRepository @Inject constructor(private val httpClient: OkHttpClient,
                                                   private val authDataSource: AuthDataSource,
                                                   private val firebaseCrashlytics: FirebaseCrashlytics) {
    suspend fun loginAndGetTicket(username: String, password: String): String? = withContext(Dispatchers.IO) {
        val firstRequest = AuthHelper.getTGTRequest(username, password)
        val tgtResponse = try {
            httpClient.newCall(firstRequest).await()
        } catch (t: Throwable) {
            Timber.e(t)
            firebaseCrashlytics.recordException(t)
            return@withContext null
        }

        Timber.d("code %s", tgtResponse.code)
        if (!tgtResponse.isSuccessful) {
            authDataSource.setLoggedOut()
            return@withContext null
        }
        // otherwise the credentials are valid
        authDataSource.setLoggedIn(username)
        val location = tgtResponse.headers["Location"]
        Timber.d("location: %s", location)
        tgtResponse.close()

        if (location == null) return@withContext null
        val ticketRequest = AuthHelper.getTicketRequest(location, AuthHelper.favoritesURL)
        val ticketResponse: Response = try {
            httpClient.newCall(ticketRequest).await()
        } catch (t: Throwable) {
            Timber.e(t)
            firebaseCrashlytics.recordException(t)
            return@withContext null
        }
        if (!ticketResponse.isSuccessful) {
            Timber.d("Ticket response not successful")
            return@withContext null
        }
        val body = ticketResponse.body
        val ticket = body?.string()?.trim { it <= ' ' } // due to Kotlin conversion
        body?.close()

        return@withContext ticket
    }

}

suspend fun Call.await() = suspendCancellableCoroutine<Response> { cont ->
    cont.invokeOnCancellation {
        try {
            cancel()
        } catch (t: Throwable) {
        }
    }
    this.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            if (cont.isCancelled) return
            cont.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            cont.resume(response)
        }
    })
}