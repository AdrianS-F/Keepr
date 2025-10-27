package com.example.keepr.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.keepr.ui.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import com.example.keepr.R

@Composable
fun SignUpScreen(
    vm: AuthViewModel,
    onGoToSignIn: () -> Unit,
    onSignedIn: () -> Unit
) {
    val s by vm.state.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Keepr", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.signup_title), style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            s.email,
            vm::updateEmail,
            label = { Text(stringResource(R.string.email_label)) },
            placeholder = { Text(stringResource(R.string.email_placeholder)) },
            singleLine = true
        )

        OutlinedTextField(
            s.password,
            vm::updatePassword,
            label = { Text(stringResource(R.string.password_label)) },
            placeholder = { Text(stringResource(R.string.password_placeholder)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            s.repeatPassword,
            vm::updateRepeat,
            label = { Text(stringResource(R.string.repeat_password_label)) },
            placeholder = { Text(stringResource(R.string.repeat_password_placeholder)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            s.firstName,
            vm::updateFirst,
            label = { Text(stringResource(R.string.first_name_label)) },
            placeholder = { Text(stringResource(R.string.first_name_placeholder)) },
            singleLine = true
        )

        OutlinedTextField(
            s.lastName,
            vm::updateLast,
            label = { Text(stringResource(R.string.last_name_label)) },
            placeholder = { Text(stringResource(R.string.last_name_placeholder)) },
            singleLine = true
        )


        if (s.error != null) {
            Spacer(Modifier.height(8.dp))
            val errMsg = when (s.error) {
                "PASSWORDS_MISMATCH" -> stringResource(R.string.err_passwords_mismatch)
                "GENERIC_ERROR"      -> stringResource(R.string.err_generic)
                "WRONG_CREDENTIALS"  -> stringResource(R.string.err_wrong_credentials)
                else                 -> s.error!!
            }
            Text(errMsg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.signUp(onSignedIn) },
            enabled = !s.loading && s.email.isNotBlank() && s.password.length >= 6 && s.firstName.isNotBlank()
        ) {
            Text(
                if (s.loading) stringResource(R.string.please_wait)
                else stringResource(R.string.signup_button)
            )
        }


        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onGoToSignIn) { Text(stringResource(R.string.signup_have_account)) }
    }
}
