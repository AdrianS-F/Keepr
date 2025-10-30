package com.example.keepr.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.keepr.R
import com.example.keepr.ui.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    vm: AuthViewModel,
    onGoToSignIn: () -> Unit,
    onSignedIn: () -> Unit
) {
    val s by vm.state.collectAsState()

    // FIKS: Kaller resetState() når skjermen forlates.
    DisposableEffect(Unit) {
        onDispose { vm.resetState() }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Sentrerer alt vertikalt for å unngå at det presses mot toppen eller bunnen
            verticalArrangement = Arrangement.Center
        ) {
            //Tittel
            Text(
                "Keepr",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.signup_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(32.dp))

            //Inputfelt
            OutlinedTextField(
                value = s.email,
                onValueChange = vm::updateEmail,
                label = { Text(stringResource(R.string.email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = s.password,
                onValueChange = vm::updatePassword,
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = s.repeatPassword,
                onValueChange = vm::updateRepeat,
                label = { Text(stringResource(R.string.repeat_password_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.password_requirements),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // FIKS: Legg til fillMaxWidth() for å tvinge teksten til venstre.
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(Modifier.height(5.dp))
            OutlinedTextField(
                value = s.firstName,
                onValueChange = vm::updateFirst,
                label = { Text(stringResource(R.string.first_name_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = s.lastName,
                onValueChange = vm::updateLast,
                label = { Text(stringResource(R.string.last_name_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(24.dp))

            //Feilmelding
            if (s.error != null) {
                val errMsg = when (s.error) {
                    "PASSWORDS_MISMATCH" -> stringResource(R.string.err_passwords_mismatch)
                    "PASSWORD_WEAK"      -> stringResource(R.string.err_password_strength)
                    "GENERIC_ERROR"      -> stringResource(R.string.err_generic)
                    "WRONG_CREDENTIALS"  -> stringResource(R.string.err_wrong_credentials)
                    else                 -> s.error!!
                }
                Text(
                    errMsg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            //Knapp
            Button(
                onClick = { vm.signUp(onSignedIn) },
                enabled = !s.loading &&
                        s.email.isNotBlank() &&
                        s.password.length >= 6 &&
                        s.password == s.repeatPassword &&
                        s.firstName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (s.loading) stringResource(R.string.please_wait)
                    else stringResource(R.string.signup_button)
                )
            }

            //Gå til Sign In
            TextButton(onClick = onGoToSignIn) {
                Text(
                    text = stringResource(R.string.signup_have_account),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}