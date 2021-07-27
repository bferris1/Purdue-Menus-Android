package com.moufee.purduemenus.ui.login

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.AuthenticationRepository
import com.moufee.purduemenus.repository.FavoritesRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @MockK
    lateinit var authenticationRepository: AuthenticationRepository

    @BindValue
    @MockK
    lateinit var favoritesRepository: FavoritesRepository

    lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun testLoginFailure() {
        coEvery { authenticationRepository.loginAndGetTicket("email@example.com", "pass") } returns null

        onView(withId(R.id.login_description)).assertCompletelyDisplayed()

        onView(withId(R.id.username)).perform(ViewActions.typeText("email@example.com"))
        onView(withId(R.id.password)).perform(ViewActions.typeText("pass"))
        onView(withId(R.id.email_sign_in_button))
            .check(matches(isCompletelyDisplayed()))
            .check(matches(withText(R.string.action_sign_in_short)))
            .perform(click())

        onView(withId(R.id.password)).check(matches(hasErrorText("This password is incorrect")))

        coVerify { authenticationRepository.loginAndGetTicket("email@example.com", "pass") }
        coVerify { favoritesRepository wasNot Called }
    }

    @Test
    fun testLoginSuccess() {
        coEvery { authenticationRepository.loginAndGetTicket("email@example.com", "pass") } returns "ticket"

        onView(withId(R.id.login_description)).assertCompletelyDisplayed()

        onView(withId(R.id.username)).perform(ViewActions.typeText("email@example.com"))
        onView(withId(R.id.password)).perform(ViewActions.typeText("pass"))
        onView(withId(R.id.email_sign_in_button))
            .check(matches(isCompletelyDisplayed()))
            .check(matches(withText(R.string.action_sign_in_short)))
            .perform(click())

        coVerify { authenticationRepository.loginAndGetTicket("email@example.com", "pass") }
        coVerify { favoritesRepository.updateFavoritesFromWeb("ticket") }

        Truth.assertThat(scenario.result.resultCode).isEqualTo(Activity.RESULT_CANCELED)
    }

    @Test
    fun testLoginInvalidInfo() {

        onView(withId(R.id.login_description)).assertCompletelyDisplayed()

        onView(withId(R.id.email_sign_in_button))
            .check(matches(isCompletelyDisplayed()))
            .check(matches(withText(R.string.action_sign_in_short)))
            .perform(click())

        onView(withId(R.id.username)).perform(click()).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.username)).perform(ViewActions.typeText("email@example.com"))
        onView(withId(R.id.email_sign_in_button)).perform(click())
        onView(withId(R.id.password)).perform(click()).check(matches(hasErrorText("This field is required")))

        coVerify { authenticationRepository wasNot Called }
        coVerify { favoritesRepository wasNot Called }
    }

}


fun ViewInteraction.assertCompletelyDisplayed(): ViewInteraction = this.check(matches(isCompletelyDisplayed()))
fun ViewInteraction.isCompletelyDisplayedWithText(textResId: Int): ViewInteraction = this.check(matches(isCompletelyDisplayed())).check(matches(withText(textResId)))