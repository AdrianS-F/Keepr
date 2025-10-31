package com.example.keepr.ui.screens.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.keepr.R
import com.example.keepr.data.ItemEntity
import com.example.keepr.ui.navigation.NavRoute
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

    val liveTitle by vm.collectionTitle.collectAsState()
    var isEditingTitle by remember { mutableStateOf(false) }
    var titleDraft by remember(liveTitle) { mutableStateOf(liveTitle) }

    // FIKS 1: Bruker en Surface med riktig bakgrunnsfarge fra temaet.
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), // Respekterer system-padding (status bar)
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp) // Gir litt pusterom i bunnen
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                // Topplinje
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            // FIKS 2: Ikoner rett på bakgrunnen må ha riktig farge.
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(8.dp))

                        if (isEditingTitle) {
                            OutlinedTextField(
                                value = titleDraft,
                                onValueChange = { titleDraft = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                if (titleDraft.isNotBlank()) vm.renameCurrentCollection(titleDraft)
                                isEditingTitle = false
                            }) { Text("Save") }
                            TextButton(onClick = {
                                titleDraft = liveTitle
                                isEditingTitle = false
                            }) { Text("Cancel") }
                        } else {
                            Text(
                                liveTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                // FIKS 3: Tittelen må ha riktig farge.
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(onClick = {
                                titleDraft = liveTitle
                                isEditingTitle = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rename items",
                                    // FIKS 4: Også dette ikonet må fargelegges.
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Søkefelt (er allerede OK, da OutlinedTextField henter farger fra temaet)
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
                            text = "No items match your search.",
                            style = MaterialTheme.typography.bodyMedium,
                            // FIKS 5: Tekstfargen må passe på bakgrunnen.
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
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
                                onDelete = { vm.delete(item) },
                                onEdit = { newName, newNotes -> vm.updateItem(item.itemId, newName, newNotes) }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(NavRoute.Add.route) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding() // Sikrer at den ikke havner bak systemlinjen
                    .padding(end = 16.dp), // Legger til litt ekstra margin fra kanten
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
}

@Composable
private fun ItemRow(
    item: ItemEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (String, String?) -> Unit
) {
    var showEdit by remember { mutableStateOf(false) }

    // FIKS 6: Kortet må bruke 'surface'-fargen for å skille seg ut.
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { showEdit = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = item.acquired, onCheckedChange = onToggle)
                    Text(
                        item.itemName,
                        style = MaterialTheme.typography.titleMedium,
                        // FIKS 7: Teksten må ha 'onSurface'-farge.
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                TextButton(onClick = onDelete) { Text(stringResource(R.string.delete)) }
            }

            item.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    // FIKS 8: Notat-teksten må også ha riktig farge.
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showEdit) {
        EditItemDialog(
            initialName = item.itemName,
            initialNotes = item.notes ?: "",
            onDismiss = { showEdit = false },
            onSave = { newName, newNotes ->
                onEdit(newName, newNotes)
                showEdit = false
            }
        )
    }
}

// EditItemDialog er OK, da AlertDialog henter farger fra temaet.
@Composable
private fun EditItemDialog(
    initialName: String,
    initialNotes: String,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var notes by remember { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onSave(name, notes.ifBlank { null }) else onDismiss()
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
