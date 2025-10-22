package com.example.keepr.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.keepr.ui.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    vm: AuthViewModel,
    onSignedIn: () -> Unit
) {
    val s by vm.state.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Keepr", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Sign in", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(s.email, vm::updateEmail, label = { Text("Email") }, singleLine = true)
        OutlinedTextField(s.password, vm::updatePassword, label = { Text("Password") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())

        if (s.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(s.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.signIn(onSignedIn) },
            enabled = !s.loading && s.email.isNotBlank() && s.password.isNotBlank()
        ) { Text(if (s.loading) "Please wait…" else "Sign in") }
    }
}
