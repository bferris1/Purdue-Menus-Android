package com.moufee.purduemenus.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moufee.purduemenus.R

/**
 * A login screen that offers login via username/password.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSignInTapped: (username: String, password: String) -> Unit,
    onFinished: () -> Unit,
) {
    val loginState by viewModel.loginState.observeAsState(LoginState())
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(loginState.shouldFinish) {
        if (loginState.shouldFinish) onFinished()
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.title_activity_login)) }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    Modifier
                        .padding(bottom = 8.dp)
                        .testTag("loginProgress")
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.login_details),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("loginDescription"),
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.hint_purdue_username)) },
                    isError = loginState.emailError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username"),
                )
                loginState.emailError?.let { error ->
                    Text(
                        text = stringResource(error),
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.testTag("usernameError"),
                    )
                }
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.prompt_password)) },
                    isError = loginState.passwordError != null,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { onSignInTapped(username, password) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("password"),
                )
                loginState.passwordError?.let { error ->
                    Text(
                        text = stringResource(error),
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.testTag("passwordError"),
                    )
                }
                Button(
                    onClick = { onSignInTapped(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .testTag("signInButton"),
                ) {
                    Text(stringResource(R.string.action_sign_in_short), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
