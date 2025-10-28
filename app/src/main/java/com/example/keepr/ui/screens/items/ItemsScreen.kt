package com.example.keepr.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.keepr.data.ItemEntity
import com.example.keepr.ui.navigation.NavRoute
import com.example.keepr.ui.viewmodel.ItemsViewModel
import com.example.keepr.ui.viewmodel.ItemsViewModelFactory
import androidx.compose.ui.res.stringResource
import com.example.keepr.R

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

    var query by rememberSaveable { mutableStateOf("") }

    val filteredItems by remember(items, query) {
        val q = query.trim().lowercase()
        mutableStateOf(
            if (q.isEmpty()) items
            else items.filter { item ->
                val name  = item.itemName.orEmpty().lowercase()
                val notes = item.notes.orEmpty().lowercase()
                val acquired = if (item.acquired) "acquired done true" else "not acquired false"
                name.contains(q) || notes.contains(q) || acquired.contains(q)
            }
        )
    }


    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)
    ) {


        Spacer(Modifier.height(12.dp))

        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Items",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search items") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )
            Spacer(Modifier.height(12.dp))


            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No collections match your search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(filteredItems, key = { it.itemId }) { item ->
                        ItemRow(
                            item = item,
                            onToggle = { checked -> vm.toggleAcquired(item, checked) },
                            onDelete = { vm.delete(item) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate(NavRoute.Add.route) }, // or open inline add
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp + padding.calculateBottomPadding()),
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
                TextButton(onClick = onDelete) { Text(stringResource(R.string.delete)) }
            }
            val notes = item.notes
            if (!notes.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(notes, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
