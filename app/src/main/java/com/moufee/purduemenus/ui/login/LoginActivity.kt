package com.moufee.purduemenus.ui.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.moufee.purduemenus.analytics.EventNames
import com.moufee.purduemenus.ui.theme.MenusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A login screen that offers login via username/password.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenusTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onSignInTapped = ::attemptLogin,
                    onFinished = { finish() },
                )
            }
        }
    }

    private fun attemptLogin(username: String, password: String) {
        mFirebaseAnalytics.logEvent(EventNames.SIGN_IN_TAPPED, Bundle())
        viewModel.loginTapped(username, password)
    }
}
