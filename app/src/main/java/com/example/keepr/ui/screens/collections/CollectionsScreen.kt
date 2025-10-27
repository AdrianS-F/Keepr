package com.example.keepr.ui.screens.collections

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.data.CollectionWithCount
import com.example.keepr.ui.theme.KeeprDark
import com.example.keepr.ui.viewmodel.CollectionsViewModel

@Composable
fun CollectionsScreen(
    padding: PaddingValues,
    onOpen: (Long) -> Unit
) {
    val vm: CollectionsViewModel = viewModel()
    val collections by vm.collections.collectAsState()

    Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
        Text("Your Collections", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(collections) { row ->
                CollectionCard(row, onClick = { onOpen(row.collection.collectionId) })
            }
        }
    }
}

@Composable
private fun CollectionCard(row: CollectionWithCount, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),  // NEW
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(row.collection.title, style = MaterialTheme.typography.titleMedium)
            if (!row.collection.description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(row.collection.description!!, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text("${row.itemCount} item(s)", style = MaterialTheme.typography.bodySmall)
        }
    }
}
