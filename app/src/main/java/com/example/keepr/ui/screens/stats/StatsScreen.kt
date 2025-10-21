package com.example.keepr.ui.screens.stats

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatsScreen(padding: PaddingValues) {
    Text("Stats", modifier = Modifier.padding(padding).padding(16.dp))
}
