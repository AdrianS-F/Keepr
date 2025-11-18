package com.example.keepr.ui.screens.collections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.data.CollectionWithCount
import com.example.keepr.ui.viewmodel.AddResult
import com.example.keepr.ui.viewmodel.CollectionsViewModel
import kotlinx.coroutines.launch

@Composable
fun CollectionsScreen(
    padding: PaddingValues,
    onOpen: (Long) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val vm: CollectionsViewModel = viewModel()
    val collections by vm.collections.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }

    val filteredCollections by remember(collections, query) {
        derivedStateOf {
            val q = query.trim().lowercase()
            if (q.isEmpty()) collections
            else collections.filter { row ->
                val title = row.collection.title.orEmpty().lowercase()
                val count = row.itemCount.toString()
                title.contains(q) || count.contains(q)
            }
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    var pendingNewCollectionTitle by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(collections, pendingNewCollectionTitle) {
        if (pendingNewCollectionTitle != null) {
            pendingNewCollectionTitle = null
        }
    }


    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Your Collections",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search collections") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )
            Spacer(Modifier.height(16.dp))

            if (filteredCollections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No collections match your search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = filteredCollections,
                        key = { it.collection.collectionId }
                    ) { row ->
                        CollectionCard(
                            row = row,
                            onOpen = { onOpen(row.collection.collectionId) },
                            onDelete = { pendingDeleteId = row.collection.collectionId }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 40.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add collection",
                modifier = Modifier.size(28.dp)
            )
        }
    }


    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newCollectionName = TextFieldValue("")
               },
            title = { Text("New Collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Collection name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val title = newCollectionName.text.trim()
                    if (title.isNotEmpty()) {
                        scope.launch {
                            when (vm.addCollection(title)) {
                                is AddResult.Success -> {
                                    pendingNewCollectionTitle = title
                                    showCreateDialog = false
                                    newCollectionName = TextFieldValue("")
                                }
                                AddResult.Duplicate -> {
                                    showCreateDialog = false
                                    newCollectionName = TextFieldValue("")
                                    snackbarHostState.showSnackbar("A collection named \"$title\" already exists.")
                                }
                                AddResult.NoUser -> {
                                    showCreateDialog = false
                                    newCollectionName = TextFieldValue("")
                                    snackbarHostState.showSnackbar("No user is signed in.")
                                }
                            }
                        }
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newCollectionName = TextFieldValue("")
                }) { Text("Cancel") }
            }
        )
    }

    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete collection?") },
            text = { Text("This will delete the collection and its items.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCollection(id)
                    pendingDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CollectionCard(
    row: CollectionWithCount,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = row.collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${row.itemCount} item(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        // FIKS 7: Ikonfargen må også passe på 'surface'.
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
