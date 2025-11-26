package com.example.keepr.ui.screens.collections

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.R
import com.example.keepr.data.CollectionWithCount
import com.example.keepr.ui.viewmodel.AddResult
import com.example.keepr.ui.viewmodel.CollectionsViewModel
import kotlinx.coroutines.launch

@Composable
fun CollectionsScreen(
    padding: PaddingValues,
    onOpen: (Long) -> Unit
) {
    val vm: CollectionsViewModel = viewModel()
    val collections by vm.collections.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
                text = stringResource(R.string.collections_title),
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
                label = {
                    Text(text = stringResource(R.string.search_collections))
                },
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
                        text = stringResource(R.string.no_collections_found),
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
                .padding(end = 16.dp),
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

    // Create collection dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newCollectionName = TextFieldValue("")
            },
            title = { Text(text = stringResource(R.string.new_collection_title)) },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text(stringResource(R.string.collection_name_label)) },
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
                                    Toast.makeText(
                                        context,
                                        "A collection named \"$title\" already exists.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                AddResult.NoUser -> {
                                    showCreateDialog = false
                                    newCollectionName = TextFieldValue("")
                                    Toast.makeText(
                                        context,
                                        "No user is signed in.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }) { Text(stringResource(R.string.add_button)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newCollectionName = TextFieldValue("")
                }) { Text(stringResource(R.string.cancel_button)) }
            }
        )
    }

    // Delete collection confirm dialog
    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.delete_collection_title)) },
            text = { Text(stringResource(R.string.delete_collection_warning)) },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCollection(id)
                    pendingDeleteId = null
                }) { Text(stringResource(R.string.delete_button)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text(stringResource(R.string.cancel_button))
                }
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
                    text = stringResource(R.string.collections_items, row.itemCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete_button)) },
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
