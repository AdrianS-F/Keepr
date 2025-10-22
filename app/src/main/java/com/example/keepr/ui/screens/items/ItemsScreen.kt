package com.example.keepr.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.data.ItemEntity
import com.example.keepr.ui.viewmodel.ItemsViewModel
import com.example.keepr.ui.viewmodel.ItemsViewModelFactory

@Composable
fun ItemsScreen(
    padding: PaddingValues,
    collectionId: Long,
    onBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as android.app.Application
    val vm: ItemsViewModel = viewModel(factory = ItemsViewModelFactory(app, collectionId))
    val items by vm.items.collectAsState()
    var newItemName by remember { mutableStateOf("") }

    // Keep layout simple: one Column that fills the space
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Simple custom top bar instead of a Material3 TopAppBar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(8.dp))
            Text("Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Button(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.1f)
            ) { Text("Add") }
        }

        Spacer(Modifier.height(12.dp))

        // LazyColumn inside a Column that already has a bounded height (fillMaxSize above)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(items, key = { it.itemId }) { item ->
                ItemRow(
                    item = item,
                    onToggle = { checked -> vm.toggleAcquired(item, checked) },
                    onDelete = { vm.delete(item) }
                )
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: ItemEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(checked = item.acquired, onCheckedChange = onToggle)
                    Text(item.itemName, style = MaterialTheme.typography.titleMedium)
                }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
            val notes = item.notes
            if (!notes.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(notes, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
