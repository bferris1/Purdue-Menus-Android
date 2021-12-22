package com.moufee.purduemenus.repository

import com.google.common.truth.Truth.assertThat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moufee.purduemenus.util.AuthHelper
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URLEncoder

class AuthenticationRepositoryTest {
    @MockK lateinit var authDataSource: AuthDataSource
    @MockK lateinit var firebaseCrashlytics: FirebaseCrashlytics
    private val httpClient = OkHttpClient.Builder().build()
    private var server: MockWebServer = MockWebServer()
    private var baseUrl: String = ""
    private lateinit var repository: AuthenticationRepository

    @Before fun setUp() {
        server = MockWebServer()
        baseUrl = server.url("/").toString().trimEnd('/')
        MockKAnnotations.init(this, relaxUnitFun = true)
        repository = AuthenticationRepository(httpClient, authDataSource, firebaseCrashlytics)
        AuthHelper.authBaseUrl = baseUrl
        AuthHelper.apiBaseUrl = baseUrl
    }

    @After fun tearDown() {
        server.shutdown()
    }

    @Test fun testLoginSuccess(): Unit = runBlocking {
        val tgtResponse = MockResponse().apply {
            setHeader(
                    "location",
                    "$baseUrl/location")
        }
        val ticketResponse = MockResponse().apply {
            setBody("theTicket   ")
        }

        server.enqueue(tgtResponse)
        server.enqueue(ticketResponse)
        val ticket = repository.loginAndGetTicket("username", "pass")
        assertThat(ticket).isEqualTo("theTicket")
        server.takeRequest().also {
            assertThat(it.requestLine).isEqualTo("POST /apps/account/cas/v1/tickets HTTP/1.1")
            val expectedBody = "username=username&password=pass"
            assertThat(it.body.readUtf8()).isEqualTo(expectedBody)
        }
        server.takeRequest().also {
            assertThat(it.requestLine).isEqualTo("POST /location HTTP/1.1")
            val expectedBody = "service=${URLEncoder.encode("$baseUrl/menus/v2/favorites", "utf-8")}"
            assertThat(it.body.readUtf8()).isEqualTo(expectedBody)
        }

        verify { authDataSource.setLoggedIn("username") }
    }

    @Test fun testLoginTicketFailure(): Unit = runBlocking {
        val tgtResponse = MockResponse().apply {
            setHeader(
                    "location",
                    "$baseUrl/location")
        }
        val ticketResponse = MockResponse().apply {
            setResponseCode(500)
        }

        server.enqueue(tgtResponse)
        server.enqueue(ticketResponse)
        val ticket = repository.loginAndGetTicket("username", "pass")
        assertThat(ticket).isNull()
        server.takeRequest().also {
            assertThat(it.requestLine).isEqualTo("POST /apps/account/cas/v1/tickets HTTP/1.1")
            val expectedBody = "username=username&password=pass"
            assertThat(it.body.readUtf8()).isEqualTo(expectedBody)
        }
        server.takeRequest().also {
            assertThat(it.requestLine).isEqualTo("POST /location HTTP/1.1")
            val expectedBody = "service=${URLEncoder.encode("$baseUrl/menus/v2/favorites", "utf-8")}"
            assertThat(it.body.readUtf8()).isEqualTo(expectedBody)
        }
        verify { authDataSource.setLoggedIn("username") }
    }

    @Test fun testLoginTgtFailure(): Unit = runBlocking {
        val tgtResponse = MockResponse().apply {
            setResponseCode(401)
        }

        server.enqueue(tgtResponse)
        val ticket = repository.loginAndGetTicket("username", "pass")
        assertThat(ticket).isNull()
        server.takeRequest().also {
            assertThat(it.requestLine).isEqualTo("POST /apps/account/cas/v1/tickets HTTP/1.1")
            val expectedBody = "username=username&password=pass"
            assertThat(it.body.readUtf8()).isEqualTo(expectedBody)
        }
        verify { authDataSource.setLoggedOut() }
    }
}