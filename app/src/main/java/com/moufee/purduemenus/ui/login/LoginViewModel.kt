package com.moufee.purduemenus.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.AuthenticationRepository
import com.moufee.purduemenus.repository.FavoritesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository, private val favoritesRepository: FavoritesRepository) :
        ViewModel() {
    private val _loginState = MutableLiveData(LoginState())
    val loginState = _loginState as LiveData<LoginState>

    fun loginTapped(username: String?, password: String?) {
        if (username.isNullOrBlank()) {
            _loginState.value = _loginState.value?.copy(emailError = R.string.error_field_required)
            return
        }
        if (password.isNullOrBlank()) {
            _loginState.value = _loginState.value?.copy(passwordError = R.string.error_field_required)
            return
        }
        _loginState.value = LoginState(isLoading = true)
        viewModelScope.launch {
            val ticket = authenticationRepository.loginAndGetTicket(username, password)
            if (ticket != null) {
                favoritesRepository.updateFavoritesFromWeb(ticket)
                _loginState.value = LoginState(shouldFinish = true)
            } else {
                //todo: differentiate auth failure from other errors
                _loginState.value = LoginState(passwordError = R.string.error_incorrect_password)
            }
        }
    }

}

data class LoginState(@StringRes val emailError: Int? = null,
                      @StringRes val passwordError: Int? = null,
                      val isLoading: Boolean = false,
                      val shouldFinish: Boolean = false)