package com.moufee.purduemenus.ui.login

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.moufee.purduemenus.R
import com.moufee.purduemenus.analytics.EventNames
import com.moufee.purduemenus.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A login screen that offers login via username/password.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var mFirebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.loginState.observe(this) { loginState ->
            binding.username.error = loginState.emailError?.let { resources.getString(it) }
            binding.password.error = loginState.passwordError?.let { resources.getString(it) }
            if (loginState.shouldFinish) {
                finish()
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        // Set up the login form.
        binding.password.setOnEditorActionListener { _: TextView?, id: Int, _: KeyEvent? ->
            if (id == R.integer.login_id || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@setOnEditorActionListener true
            }
            false
        }
        val mEmailSignInButton = findViewById<Button>(R.id.email_sign_in_button)
        mEmailSignInButton.setOnClickListener { attemptLogin() }
    }
    private fun attemptLogin() {
        mFirebaseAnalytics.logEvent(EventNames.SIGN_IN_TAPPED, Bundle())
        viewModel.loginTapped(binding.username.text.toString(), binding.password.text.toString())

    }
}

