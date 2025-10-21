package com.example.keepr.ui.screens.collections

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun CollectionsScreen(padding: PaddingValues) {
    Text("Collections", modifier = Modifier.padding(padding).padding(16.dp))
}
