package com.moufee.purduemenus.ui.login

import android.app.Activity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.moufee.purduemenus.repository.AuthenticationRepository
import com.moufee.purduemenus.repository.FavoritesRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @BindValue
    val authenticationRepository: AuthenticationRepository = mockk(relaxed = true, relaxUnitFun = true)

    @BindValue
    val favoritesRepository: FavoritesRepository = mockk(relaxed = true, relaxUnitFun = true)

    @Test
    fun testLoginFailure() {
        coEvery { authenticationRepository.loginAndGetTicket("email@example.com", "pass") } returns null

        composeRule.onNodeWithTag("loginDescription").assertIsDisplayed()

        composeRule.onNodeWithTag("username").performTextInput("email@example.com")
        composeRule.onNodeWithTag("password").performTextInput("pass")
        composeRule.onNodeWithTag("signInButton").assertIsDisplayed().performClick()

        composeRule.onNodeWithTag("passwordError").assertIsDisplayed()

        coVerify { authenticationRepository.loginAndGetTicket("email@example.com", "pass") }
        coVerify { favoritesRepository wasNot Called }
    }

    @Test
    fun testLoginSuccess() {
        coEvery { authenticationRepository.loginAndGetTicket("email@example.com", "pass") } returns "ticket"

        composeRule.onNodeWithTag("loginDescription").assertIsDisplayed()

        composeRule.onNodeWithTag("username").performTextInput("email@example.com")
        composeRule.onNodeWithTag("password").performTextInput("pass")
        composeRule.onNodeWithTag("signInButton").assertIsDisplayed().performClick()

        coVerify { authenticationRepository.loginAndGetTicket("email@example.com", "pass") }
        coVerify { favoritesRepository.updateFavoritesFromWeb("ticket") }

        Truth.assertThat(composeRule.activityRule.scenario.result.resultCode).isEqualTo(Activity.RESULT_CANCELED)
    }

    @Test
    fun testLoginInvalidInfo() {
        composeRule.onNodeWithTag("loginDescription").assertIsDisplayed()

        composeRule.onNodeWithTag("signInButton").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("usernameError").assertIsDisplayed()

        composeRule.onNodeWithTag("username").performTextInput("email@example.com")
        composeRule.onNodeWithTag("signInButton").performClick()
        composeRule.onNodeWithTag("passwordError").assertIsDisplayed()

        coVerify { authenticationRepository wasNot Called }
        coVerify { favoritesRepository wasNot Called }
    }
}
