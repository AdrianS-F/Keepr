package com.example.keepr.ui.screens.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.R
import com.example.keepr.data.CollectionEntity
import com.example.keepr.ui.viewmodel.AddViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // ?? Hva er dette, må fjernes
@Composable
fun AddScreen(padding: PaddingValues) {
    // ViewModel og UI-state (uendret)
    val vm: AddViewModel = viewModel()
    val collections by vm.collections.collectAsState()
    var selectedImage by remember { mutableStateOf<Int?>(R.drawable.ic_launcher_foreground) }
    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemDescription by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCollection by remember { mutableStateOf<CollectionEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // LaunchedEffects (uendret)
    LaunchedEffect(collections) {
        if (selectedCollection == null && collections.isNotEmpty()) {
            selectedCollection = collections.first()
        }
    }
    var pendingNewCollectionTitle by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(collections, pendingNewCollectionTitle) {
        val wanted = pendingNewCollectionTitle
        if (wanted != null) {
            val match = collections.find { it.title == wanted }
            if (match != null) {
                selectedCollection = match
                pendingNewCollectionTitle = null
            }
        }
    }

    Scaffold(
        // FIKS 1: Topplinjen får nå riktige farger fra temaet
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Item") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        // FIKS 2: Hovedbakgrunnen bruker nå fargen fra temaet
        containerColor = MaterialTheme.colorScheme.background,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 📸 Bilde
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        // FIKS 3: Bilde-placeholder bruker nå 'surface'-fargen for å matche kortene
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .clickable { /* TODO */ },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = selectedImage ?: R.drawable.ic_launcher_foreground),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 🏷️ Navn & ✍️ Beskrivelse (OutlinedTextField henter farger automatisk fra temaet, så de er OK)
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // 📂 Collection dropdown
                Text(
                    text = "Collection:",
                    style = MaterialTheme.typography.labelLarge,
                    // FIKS 4: Teksten må få riktig farge for bakgrunnen
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))

                if (collections.isEmpty()) {
                    // OutlinedButton henter farger automatisk, så den er OK
                    OutlinedButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("➕ Add new collection")
                    }
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedCollection?.title ?: "Select a collection")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            collections.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.title) },
                                    onClick = {
                                        selectedCollection = c
                                        expanded = false
                                    }
                                )
                            }
                            Divider()
                            DropdownMenuItem(
                                text = { Text("+ New collection") },
                                onClick = {
                                    expanded = false
                                    showDialog = true
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                // 💾 Lagre item (Button henter farger automatisk, så den er OK)
                Button(
                    onClick = {
                        // ... (din eksisterende logikk er uendret)
                        when {
                            itemName.text.isBlank() -> {
                                scope.launch { snackbarHostState.showSnackbar("Please enter an item name.") }
                            }
                            selectedCollection == null -> {
                                scope.launch { snackbarHostState.showSnackbar("Please select or create a collection.") }
                            }
                            else -> {
                                vm.addItem(
                                    collectionId = selectedCollection!!.collectionId,
                                    itemName = itemName.text,
                                    description = itemDescription.text,
                                    imgUri = null
                                )
                                itemName = TextFieldValue("")
                                itemDescription = TextFieldValue("")
                                scope.launch { snackbarHostState.showSnackbar("Saved in '${selectedCollection!!.title}': ${itemName.text.ifBlank { "New item" }}") }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save item")
                }
            }
        }
    )

    // Dialog (AlertDialog henter farger automatisk, så den er OK)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Collection") },
            text = { /* ... */ },
            confirmButton = { /* ... */ },
            dismissButton = { /* ... */ }
        )
    }
}
