package com.example.keepr.ui.screens.profile // screen for profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // imports the rounded edge
import androidx.compose.material.icons.Icons // imports the icons
import androidx.compose.material.icons.outlined.Badge // imports the badge icon
import androidx.compose.material.icons.outlined.Logout // imports the logout icon
import androidx.compose.material.icons.outlined.PhotoCamera // imports the camera icon
import androidx.compose.material.icons.outlined.Translate // imports the translate icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keepr.R
import com.example.keepr.ui.components.ChangeLanguageDialog

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    val vm: ProfileViewModel = viewModel()
    val state by vm.state.collectAsState()
    val user = state.user

    // Hovedcontaineren som bruker bakgrunnsfargen fra det nye temaet
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // En dekorativ boks på toppen for visuell stil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                // FIKSET: Bruker en variant av hovedfargen for å matche temaet
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                // FIKSET: Tekstfarge som passer på bakgrunnen
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(25.dp))

            Image(
                painter = painterResource(id = R.drawable.sharu),
                contentDescription = stringResource(R.string.change_profile_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(175.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.height(25.dp))

            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "",
                // FIKSET: Tekstfarge som passer på bakgrunnen
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.collections_count, state.collectionCount),
                // FIKSET: En litt dusere tekstfarge
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontSize = 18.sp
            )

            Spacer(Modifier.height(24.dp))

            // Kortet som inneholder innstillingene
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    // FIKSET: Bruker "surface" fargen fra temaet for kortet
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_title),
                        // FIKSET: Tekstfarge som passer på "surface"
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(8.dp))

                    // Gjenbruker disse fargene for alle rader og ikoner
                    val contentColor = MaterialTheme.colorScheme.onSurface
                    val dividerColor = contentColor.copy(alpha = 0.2f)

                    // Endre profilbilde
                    ProfileRow(
                        text = stringResource(R.string.change_profile_picture),
                        icon = Icons.Outlined.PhotoCamera,
                        contentColor = contentColor,
                        onClick = {}
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    // Endre navn
                    ProfileRow(
                        text = stringResource(R.string.change_name),
                        icon = Icons.Outlined.Badge,
                        contentColor = contentColor,
                        onClick = {}
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    // Endre språk
                    ProfileRow(
                        text = stringResource(R.string.change_language),
                        icon = Icons.Outlined.Translate,
                        contentColor = contentColor,
                        onClick = { showLanguageDialog = true }
                    )
                    Divider(color = dividerColor, thickness = 1.dp)

                    // Logg ut
                    ProfileRow(
                        text = stringResource(R.string.log_out),
                        icon = Icons.Outlined.Logout,
                        contentColor = contentColor,
                        onClick = onLogout
                    )

                    if (showLanguageDialog) {
                        ChangeLanguageDialog(onDismiss = { showLanguageDialog = false })
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Slett bruker-knapp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(18.dp))
                    // FIKSET: Bruker en svak "error"-farge for bakgrunnen
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                    .clickable {}
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_user),
                    // FIKSET: Bruker "error"-fargen fra temaet for teksten
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * En gjenbrukbar Composable for å unngå kodeduplisering for radene i profilen.
 */
@Composable
private fun ProfileRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
