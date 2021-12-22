package com.moufee.purduemenus.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.AuthenticationRepository
import com.moufee.purduemenus.repository.FavoritesRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var authenticationRepository: AuthenticationRepository
    @MockK lateinit var favoritesRepository: FavoritesRepository
    @MockK lateinit var observer: Observer<LoginState>
    private lateinit var viewModel: LoginViewModel

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(TestCoroutineDispatcher())
        viewModel = LoginViewModel(authenticationRepository, favoritesRepository)
        viewModel.loginState.observeForever(observer)
    }

    @Test fun `log in successfully`() {
        coEvery { authenticationRepository.loginAndGetTicket("username", "pass") } returns "ticket"
        viewModel.loginTapped("username", "pass")
        coVerify { authenticationRepository.loginAndGetTicket("username", "pass") }
        coVerify { favoritesRepository.updateFavoritesFromWeb("ticket") }
        verifyOrder {
            observer.onChanged(LoginState(isLoading = true))
            observer.onChanged(LoginState(shouldFinish = true))
        }
    }

    @Test fun `form password error`(){
        viewModel.loginTapped("username", null)
        verify { observer.onChanged(LoginState(passwordError = R.string.error_field_required)) }
    }

    @Test fun `form username error`(){
        viewModel.loginTapped("", "pass")
        verify { observer.onChanged(LoginState(emailError = R.string.error_field_required)) }
    }
}