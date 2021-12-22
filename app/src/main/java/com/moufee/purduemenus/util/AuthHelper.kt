package com.moufee.purduemenus.util

import androidx.annotation.VisibleForTesting
import okhttp3.FormBody
import okhttp3.Request

object AuthHelper {
    @VisibleForTesting
    var apiBaseUrl = "https://api.hfs.purdue.edu"

    @VisibleForTesting
    var authBaseUrl = "https://www.purdue.edu"

    val favoritesURL get() = "$apiBaseUrl/menus/v2/favorites"
    private val ticketUrl get() = "$authBaseUrl/apps/account/cas/v1/tickets"

    fun getTGTRequest(username: String, password: String): Request {
        val formBody: FormBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build()

        return Request.Builder()
                .url(ticketUrl)
                .post(formBody)
                .build()
    }

    fun getTicketRequest(location: String, service: String): Request {
        val ticketRequestBody: FormBody = FormBody.Builder()
                .add("service", service)
                .build()
        return Request.Builder()
                .url(location)
                .post(ticketRequestBody)
                .build()
    }
}