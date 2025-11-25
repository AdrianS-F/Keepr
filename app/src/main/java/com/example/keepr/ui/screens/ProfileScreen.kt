package com.example.keepr.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.R
import com.example.keepr.ui.components.ChangeLanguageDialog
import com.example.keepr.ui.components.ChangeNameDialog
import com.example.keepr.ui.components.DeleteUserDialog
import com.example.keepr.ui.viewmodel.ProfileViewModel
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var wrongPass by remember { mutableStateOf(false) }
    val ctx = LocalContext.current


    val vm: ProfileViewModel = viewModel()
    val state by vm.state.collectAsState()
    val user = state.user

    val openImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                try{
                    ctx.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) { }

                vm.updatePhoto(it.toString())
            }

        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 120.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp
            )

            Spacer(Modifier.height(25.dp))

            val avatarUri = state.avatarUri

            Image(
                painter = if (avatarUri.isNullOrBlank())
                    painterResource(id = R.drawable.placeholder_profile)
                else
                    rememberAsyncImagePainter(model = avatarUri),
                contentDescription = stringResource(R.string.change_profile_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(175.dp)
                    .clip(CircleShape)
                    .clickable { openImagePicker.launch(arrayOf("image/*")) }
            )

            Spacer(Modifier.height(25.dp))

            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.collections_count, state.collectionCount),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontSize = 18.sp
            )

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    Spacer(Modifier.height(8.dp))

                    val contentColor = MaterialTheme.colorScheme.onSurface
                    val dividerColor = contentColor.copy(alpha = 0.2f)

                    ProfileRow(
                        text = stringResource(R.string.change_profile_picture),
                        icon = Icons.Outlined.PhotoCamera,
                        contentColor = contentColor,
                        onClick = { openImagePicker.launch(arrayOf("image/*")) }
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    ProfileRow(
                        text = stringResource(R.string.change_name),
                        icon = Icons.Outlined.Badge,
                        contentColor = contentColor,
                        onClick = { showNameDialog = true }
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    ProfileRow(
                        text = stringResource(R.string.change_language),
                        icon = Icons.Outlined.Translate,
                        contentColor = contentColor,
                        onClick = { showLanguageDialog = true }
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    ProfileRow(
                        text = stringResource(R.string.log_out),
                        icon = Icons.Outlined.Logout,
                        contentColor = contentColor,
                        onClick = onLogout
                    )

                    if (showLanguageDialog) {
                        ChangeLanguageDialog(onDismiss = { showLanguageDialog = false })
                    }

                    if (showNameDialog && user != null) {
                        ChangeNameDialog(
                            firstInit = user.firstName,
                            lastInit = user.lastName,
                            onSave = { f, l ->
                                val fullName = "${f.trim()} ${l.trim()}".trim()

                                if (fullName.length > 35) {
                                    Toast.makeText(
                                        ctx,
                                        ctx.getString(R.string.name_too_long),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    false
                                } else {
                                    vm.updateName(f, l)
                                    showNameDialog = false
                                    true
                                }
                            },
                            onDismiss = { showNameDialog = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { showDeleteDialog = true }
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_user),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showDeleteDialog) {
                DeleteUserDialog(
                    onDelete = { pass ->
                        vm.deleteUser(pass) { ok ->
                            if (ok) {
                                Toast.makeText(
                                    ctx,
                                    ctx.getString(R.string.user_deleted),
                                    Toast.LENGTH_LONG
                                ).show()
                                showDeleteDialog = false
                                onLogout()
                            } else {
                                wrongPass = true
                            }
                        }
                    },
                    onDismiss = { showDeleteDialog = false; wrongPass = false },
                    wrongPassword = wrongPass
                )
            }
        }
    }
}


@Composable
private fun ProfileRow(
    text: String,
    icon: ImageVector,
    contentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

