package com.example.keepr.ui.screens.add

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

// Denne annotasjonen trengs fordi CenterAlignedTopAppBar er "eksperimentell" kode
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(padding: PaddingValues) {

    // ----------------------------- //
    // 🧠 1. STATE (variabler som husker verdier når skjermen oppdateres)
    // ----------------------------- //

    // Midlertidig bilde (vi bruker launcher-ikonet som placeholder)
    var selectedImage by remember { mutableStateOf<Int?>(R.drawable.ic_launcher_foreground) }

    // Tekstfeltene for navn og beskrivelse
    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemDescription by remember { mutableStateOf(TextFieldValue("")) }

    // Liste over collections (hardkodet for nå – vi later som det kommer fra database)
    var collections = remember { mutableStateListOf("Clothes", "Books", "Electronics") }
    var selectedCollection by remember { mutableStateOf(collections.firstOrNull()) }

    // Variabler for "lag ny collection"-dialogen
    var showDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }

    // ----------------------------- //
    // 🧱 2. UI (alt du ser på skjermen)
    // ----------------------------- //

    Scaffold(
        // Toppen av skjermen (header med tittel)
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Item") } // viser "Add Item" øverst
            )
        },
        // Hovedinnholdet
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    // Padding brukes for å unngå at innholdet havner bak topBar/bottomBar
                    .padding(padding)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ----------------------------- //
                // 📸 BILDE (placeholder)
                // ----------------------------- //
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable {
                            // Her kan du senere legge til bildevelger (kamera / galleri)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = selectedImage ?: R.drawable.ic_launcher_foreground
                        ),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ----------------------------- //
                // 🏷️ TEKSTFELT FOR ITEM NAVN
                // ----------------------------- //
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ----------------------------- //
                // ✍️ TEKSTFELT FOR BESKRIVELSE
                // ----------------------------- //
                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ----------------------------- //
                // 📂 DROPDOWN FOR COLLECTIONS
                // ----------------------------- //
                Text(text = "Collection:", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                var expanded by remember { mutableStateOf(false) } // om menyen er åpen

                Box {
                    // Knapp som åpner menyen
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(selectedCollection ?: "Select collection")
                    }

                    // Selve menyen
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // For hver collection i listen
                        collections.forEach { collection ->
                            DropdownMenuItem(
                                text = { Text(collection) },
                                onClick = {
                                    selectedCollection = collection
                                    expanded = false // lukker menyen
                                }
                            )
                        }

                        // Linje og valget for "ny collection"
                        Divider()
                        DropdownMenuItem(
                            text = { Text("➕ New collection") },
                            onClick = {
                                expanded = false
                                showDialog = true // viser dialogen under
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // ----------------------------- //
                // 💾 "SAVE ITEM" KNAPP
                // ----------------------------- //
                Button(
                    onClick = {
                        // Foreløpig bare print til loggen
                        println(
                            "Item saved: ${itemName.text} (${itemDescription.text}) in $selectedCollection"
                        )

                        // Senere kan du lagre i databasen her
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save item")
                }
            }
        }
    )

    // ----------------------------- //
    // 🪣 DIALOG: LEGG TIL NY COLLECTION
    // ----------------------------- //
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
                    // Legger til ny collection hvis tekstfeltet ikke er tomt
                    if (newCollectionName.text.isNotBlank()) {
                        collections.add(newCollectionName.text)
                        selectedCollection = newCollectionName.text
                    }
                    // Nullstill og lukk dialog
                    newCollectionName = TextFieldValue("")
                    showDialog = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}