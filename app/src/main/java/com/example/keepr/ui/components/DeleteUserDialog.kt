package com.example.keepr.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import com.example.keepr.R
import androidx.compose.ui.Modifier

@Composable
fun DeleteUserDialog(
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit,
    wrongPassword: Boolean
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_user_title)) },
        text = {
            Column {
                Text(stringResource(R.string.delete_user_warning))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (wrongPassword) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.err_wrong_password),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {
            TextButton(onClick = { onDelete(password) }, enabled = password.isNotBlank()) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}