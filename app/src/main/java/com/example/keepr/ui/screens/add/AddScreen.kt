package com.example.keepr.ui.screens.add

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.keepr.R
import com.example.keepr.data.CollectionEntity
import kotlinx.coroutines.launch

// TopAppBar er merket eksperimentell
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(padding: PaddingValues) {

    // 1) Hent ViewModel og strøm av collections fra databasen
    val vm: AddViewModel = viewModel()
    val collections by vm.collections.collectAsState() // oppdaterer UI når DB endres

    // 2) UI-state (det brukeren skriver/velger)
    var selectedImage by remember { mutableStateOf<Int?>(R.drawable.ic_launcher_foreground) }
    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemDescription by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCollection by remember { mutableStateOf<CollectionEntity?>(null) }

    // Dialog for ny collection
    var showDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }

    // Snackbar for synlig tilbakemelding
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 3) Når collections kommer fra DB første gang, velg automatisk første
    LaunchedEffect(collections) {
        if (selectedCollection == null && collections.isNotEmpty()) {
            selectedCollection = collections.first()
        }
    }

    // Når vi legger til en ny collection, husk navnet og velg den automatisk når DB-oppdateringen kommer
    var pendingNewCollectionTitle by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(collections, pendingNewCollectionTitle) {
        val wanted = pendingNewCollectionTitle
        if (wanted != null) {
            val match = collections.find { it.title == wanted }
            if (match != null) {
                selectedCollection = match
                pendingNewCollectionTitle = null // ferdig
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Add Item") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // <- viser snackbars
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 📸 Bilde (placeholder)
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            // TODO: legg til galleri/kamera senere
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = selectedImage ?: R.drawable.ic_launcher_foreground),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 🏷️ Navn
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // ✍️ Beskrivelse
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // 📂 Collection dropdown
                Text(text = "Collection:", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))

                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        enabled = collections.isNotEmpty() // grå ut hvis ingen finnes enda
                    ) {
                        Text(selectedCollection?.title ?: "No collections")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
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
                            text = { Text("➕ New collection") },
                            onClick = {
                                expanded = false
                                showDialog = true
                            }
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))

                // 💾 Lagre item i DB
                Button(
                    onClick = {
                        when {
                            itemName.text.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter an item name.")
                                }
                            }
                            selectedCollection == null -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please select or create a collection.")
                                }
                            }
                            else -> {
                                // Kall VM for å lagre i databasen
                                vm.addItem(
                                    collectionId = selectedCollection!!.collectionId,
                                    itemName = itemName.text,
                                    description = itemDescription.text,
                                    imgUri = null // legg til når bildevalg er klart
                                )

                                // Tøm felter + vis snackbar
                                itemName = TextFieldValue("")
                                itemDescription = TextFieldValue("")
                                scope.launch {
                                    snackbarHostState.showSnackbar("Saved '${selectedCollection!!.title}': ${itemName.text.ifBlank { "New item" }}")
                                    // NB: vi kunne navigert tilbake her hvis dere ønsker
                                }
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

    // 🪣 Dialog: lag ny collection
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Collection name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val title = newCollectionName.text.trim()
                    if (title.isNotEmpty()) {
                        // Be VM legge til i DB
                        vm.addCollection(title)
                        // Marker at vi ønsker å auto-velge denne når DB/Flow oppdateres
                        pendingNewCollectionTitle = title
                    }
                    newCollectionName = TextFieldValue("")
                    showDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}