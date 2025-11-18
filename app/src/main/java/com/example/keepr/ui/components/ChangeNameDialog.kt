package com.example.keepr.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.keepr.R

@Composable
fun ChangeNameDialog (
    firstInit: String,
    lastInit: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var first by rememberSaveable { mutableStateOf(firstInit) }
    var last by rememberSaveable { mutableStateOf(lastInit) }

    AlertDialog (
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.change_name_title)) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = first,
                    onValueChange = { first = it },
                    label = { Text(stringResource(R.string.first_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp)) // gives us space
                OutlinedTextField(
                    value = last,
                    onValueChange = { last = it },
                    label = { Text(stringResource(R.string.last_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (first.isNotBlank() && last.isNotBlank()) {
                        onSave(first.trim(), last.trim())
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_name_changed),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_fill_both),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}