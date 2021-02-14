package com.vanpra.amblor.ui.layouts.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.vanpra.amblor.LocalNavHostController
import com.vanpra.amblor.Screen
import com.vanpra.amblor.util.BackButtonTitle
import com.vanpra.amblor.util.ErrorOutlinedTextField
import com.vanpra.amblor.util.PrimaryTextButton
import java.util.regex.Pattern

class EmailSignupState {
    var email = TextInputState("Email", defaultError = "Invalid Email") {
        Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
    var username = TextInputState("Username")
    var password = TextInputState("Password") { password ->
        if (password.length < 8) {
            return@TextInputState false
        }

        val patterns = listOf(
            ".*[0-9].*",
            ".*[A-Z].*",
            ".*[a-z].*",
            ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        )

        patterns.forEach {
            if (!Pattern.compile(it).matcher(password).matches()) {
                return@TextInputState false
            }
        }

        return@TextInputState true
    }

    var confirmPassword =
        TextInputState("Confirm Password", defaultError = "Passwords don't match") {
            it == password.text
        }

    fun detailsValid() =
        username.isValid() && email.isValid() && password.isValid() && confirmPassword.isValid()
}

@Composable
fun EmailSignup(authViewModel: AuthViewModel) {
    val focusManager = LocalFocusManager.current
    val authNavHost = LocalNavHostController.current

    var showingPasswordList by remember { mutableStateOf(false) }

    Column(
        Modifier
            .clickable(
                onClick = { focusManager.clearFocus() },
                indication = null,
                interactionState = InteractionState()
            )
            .fillMaxSize()
    ) {
        BackButtonTitle(title = "Sign up") {
            authNavHost.navigate(Screen.Login.route)
        }

        Column(Modifier.padding(start = 16.dp, end = 16.dp)) {
            ErrorOutlinedTextField(
                inputState = authViewModel.signupState.email,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                nextInput = authViewModel.signupState.username
            )

            Spacer(modifier = Modifier.height(8.dp))

            ErrorOutlinedTextField(
                inputState = authViewModel.signupState.username,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                nextInput = authViewModel.signupState.password
            )

            Spacer(modifier = Modifier.height(8.dp))

            ErrorOutlinedTextField(
                inputState = authViewModel.signupState.password,
                modifier = Modifier.onFocusChanged {
                    showingPasswordList = it.isFocused
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                nextInput = authViewModel.signupState.confirmPassword,
                showErrorText = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordCriteria(
                signUpModel = authViewModel.signupState,
                showing = showingPasswordList
            )

            ErrorOutlinedTextField(
                inputState = authViewModel.signupState.confirmPassword,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryTextButton(
                text = "Complete Sign up",
                enabled = authViewModel.signupState.detailsValid(),
                onClick = { authViewModel.signUpWithEmail() }
            )
        }
    }
}

@Composable
private fun PasswordCriteria(
    signUpModel: EmailSignupState,
    showing: Boolean
) {
    AnimatedVisibility(
        visible = showing,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            PasswordCriteriaItem(
                text = "At least 8 characters long",
                satisfied = signUpModel.password.text.length >= 8
            )
            PasswordCriteriaItem(
                text = "At least 1 uppercase letter",
                satisfied = Pattern.compile(
                    ".*[A-Z].*"
                ).matcher(signUpModel.password.text).matches()
            )
            PasswordCriteriaItem(
                text = "At least 1 number",
                satisfied = Pattern.compile(
                    ".*[0-9].*"
                ).matcher(signUpModel.password.text).matches()
            )
            PasswordCriteriaItem(
                text = "At least 1 special character",
                satisfied = Pattern.compile(
                    ".*[~!@#\$%^&*()\\-_=+|\\[{\\]};:'\",<.>/?].*"
                ).matcher(signUpModel.password.text).matches()
            )
        }
    }
}

@Composable
private fun PasswordCriteriaItem(text: String, satisfied: Boolean) {
    Row(Modifier.padding(8.dp)) {
        val imageTint = ColorFilter.tint(if (satisfied) Color.Green else Color.Red)
        val image = if (satisfied) Icons.Default.Check else Icons.Default.Close
        Image(image, contentDescription = null, colorFilter = imageTint)
        Text(
            text,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
