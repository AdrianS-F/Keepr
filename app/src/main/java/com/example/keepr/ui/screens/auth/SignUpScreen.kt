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

@Composable
fun SignUpScreen(
    vm: AuthViewModel,
    onGoToSignIn: () -> Unit,
    onSignedIn: () -> Unit
) {
    val s by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Tittel ---
        Text("Keepr", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Sign up", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(32.dp))

        // --- Inputfelt ---
        OutlinedTextField(
            value = s.email,
            onValueChange = vm::updateEmail,
            label = { Text("Email") },
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
            label = { Text("Password") },
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
            label = { Text("Repeat password") },
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
            value = s.firstName,
            onValueChange = vm::updateFirst,
            label = { Text("First name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = s.lastName,
            onValueChange = vm::updateLast,
            label = { Text("Last name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        // --- Feilmelding ---
        if (s.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(s.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))

        // --- Knapp ---
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
            shape = RoundedCornerShape(50.dp), // rund knapp
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(if (s.loading) "Please wait…" else "Create account")
        }

        Spacer(Modifier.height(16.dp))

        // --- Gå til Sign In ---
        TextButton(onClick = onGoToSignIn) {
            Text("Already have an account? Sign in")
        }
    }
}