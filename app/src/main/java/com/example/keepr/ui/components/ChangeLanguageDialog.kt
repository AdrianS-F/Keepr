package com.example.keepr.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.keepr.R
import com.example.keepr.data.SettingsManager

@Composable
fun ChangeLanguageDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val settings = remember { SettingsManager(context) }
    val current = settings.getLanguage()
    var selected by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_title)) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                LanguageRow(
                    label = stringResource(R.string.english),
                    selected = selected == "en",
                    onClick = { selected = "en" }
                )
                Spacer(Modifier.height(8.dp))
                LanguageRow(
                    label = stringResource(R.string.norwegian_bokmaal),
                    selected = selected == "nb",
                    onClick = { selected = "nb" }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                settings.setLanguage(selected)
                Toast.makeText(
                    context,
                    context.getString(R.string.restart_to_apply),
                    Toast.LENGTH_LONG
                ).show()
                onDismiss()
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun LanguageRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        RadioButton(selected = selected, onClick = onClick)
    }
}