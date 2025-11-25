package com.example.keepr.ui.screens

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.R
import com.example.keepr.data.CollectionEntity
import com.example.keepr.ui.viewmodel.AddViewModel
import com.example.keepr.ui.viewmodel.AddItemResult
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.ui.res.stringResource
import com.example.keepr.ui.viewmodel.AddResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    padding: PaddingValues,
    onSaved:(Long) ->Unit,
    initialCollectionId: Long?

) {
    val context = LocalContext.current
    val vm: AddViewModel = viewModel()
    val collections by vm.collections.collectAsState()
    val ctx = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    val openImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                try {
                    ctx.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) { }

                selectedImageUri = it.toString()
            }
        }
    )

    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemDescription by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCollection by remember { mutableStateOf<CollectionEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf(TextFieldValue("")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(collections, initialCollectionId) {
        if (selectedCollection == null && initialCollectionId != null) {
            selectedCollection = collections.find { it.collectionId == initialCollectionId }
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
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                newCollectionName = TextFieldValue("")
            },
            title = { Text(stringResource(R.string.new_collection_title)) },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text(stringResource(R.string.collection_name_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val title = newCollectionName.text.trim()
                        if (title.isNotEmpty()) {
                            showDialog = false
                            newCollectionName = TextFieldValue("")
                            pendingNewCollectionTitle = title
                            scope.launch {
                                when (val result = vm.addCollection(title)) {

                                    is AddResult.Success -> {
                                    }

                                    is AddResult.Duplicate -> {
                                        snackbarHostState.showSnackbar(
                                            "A collection named \"$title\" already exists."
                                        )
                                    }

                                    is AddResult.NoUser -> {
                                        snackbarHostState.showSnackbar(
                                            "No user is signed in."
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.add_button))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    newCollectionName = TextFieldValue("")
                }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.add_item_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)   // SCROLL ENABLED
                    .fillMaxWidth(),               // do NOT fill height
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //  Bilde
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .clickable { openImagePicker.launch(arrayOf("image/*")) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri == null) {
                        Icon(
                            imageVector = Icons.Outlined.AddAPhoto,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text(stringResource(R.string.item_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = itemDescription,
                    onValueChange = { itemDescription = it },
                    label = { Text(stringResource(R.string.item_description_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 200.dp)
                        .verticalScroll(rememberScrollState()),
                    maxLines = 8
                )


                Spacer(Modifier.height(12.dp))

                //Collection dropdown
                Text(
                    text = stringResource(R.string.item_collection_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))

                if (collections.isEmpty()) {
                    OutlinedButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.item_add_new_collection))
                    }
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedCollection?.title ?: stringResource(R.string.item_add_new_collection))
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

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.new_collection_button)) },
                                onClick = {
                                    expanded = false
                                    showDialog = true
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = {
                        when {
                            itemName.text.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(message = context.getString(R.string.enter_item_name))
                                }
                            }
                            selectedCollection == null -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(message = context.getString(R.string.select_or_create_collection))
                                }
                            }
                            else -> {
                                val cid = selectedCollection!!.collectionId
                                scope.launch {
                                    when (val res = vm.addItem(
                                        collectionId = cid,
                                        itemName = itemName.text,
                                        description = itemDescription.text,
                                        imgUri = selectedImageUri
                                    )) {
                                        is AddItemResult.Success -> {
                                            itemName = TextFieldValue("")
                                            itemDescription = TextFieldValue("")
                                            onSaved(cid)
                                        }
                                        AddItemResult.Duplicate -> {
                                            snackbarHostState.showSnackbar(
                                                message = context.getString(
                                                    R.string.item_exists,
                                                    itemName.text.trim()
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.item_save_item))
                }


            }
        }
    )
}
