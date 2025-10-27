package com.example.keepr.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.keepr.data.ItemEntity
import com.example.keepr.ui.viewmodel.ItemsViewModel
import com.example.keepr.ui.viewmodel.ItemsViewModelFactory

@Composable
fun ItemsScreen(
    padding: PaddingValues,
    collectionId: Long,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val app = LocalContext.current.applicationContext as android.app.Application
    val vm: ItemsViewModel = viewModel(factory = ItemsViewModelFactory(app, collectionId))
    val items by vm.items.collectAsState()

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        // content layer (header + list)
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text("Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 88.dp), // keep last row visible under the FAB
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.itemId }) { item ->
                    ItemRow(
                        item = item,
                        onToggle = { checked -> vm.toggleAcquired(item, checked) },
                        onDelete = { vm.delete(item) }
                    )
                }
            }
        }

        // floating action button layer
        androidx.compose.material3.FloatingActionButton(
            onClick = { navController.navigate("add") }, // or NavRoute.Add.route
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add item",
                modifier = Modifier.size(28.dp)
            )
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
