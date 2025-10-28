package com.example.keepr.ui.screens.auth


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.keepr.ui.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import com.example.keepr.R

@Composable
fun SignInScreen(
    vm: AuthViewModel,
    onSignedIn: () -> Unit
) {
    val s by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Keepr", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(32.dp))

        // Runde tekstfelt
        OutlinedTextField(
            value = s.email,
            onValueChange = vm::updateEmail,
            label = { Text(stringResource(R.string.email_label)) },
            placeholder = { Text(stringResource(R.string.email_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = s.password,
            onValueChange = vm::updatePassword,
            label = { Text(stringResource(R.string.password_label)) },
            placeholder = { Text(stringResource(R.string.password_placeholder)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        if (s.error != null) {
            Spacer(Modifier.height(8.dp))
            val errMsg = when (s.error) {
                "WRONG_CREDENTIALS"  -> stringResource(R.string.err_wrong_credentials)
                "PASSWORDS_MISMATCH" -> stringResource(R.string.err_passwords_mismatch)
                "GENERIC_ERROR"      -> stringResource(R.string.err_generic)
                else                 -> s.error!!
            }
            Text(errMsg, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        // Rund og moderne knapp
        Button(
            onClick = { vm.signIn(onSignedIn) },
            enabled = !s.loading && s.email.isNotBlank() && s.password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(50.dp), // helt rund
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                if (s.loading) stringResource(R.string.please_wait)
                else stringResource(R.string.login_button)
            )
        }
    }
}