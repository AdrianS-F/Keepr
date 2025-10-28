package com.example.keepr.ui.screens.profile // screen for profile

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // imports the rounded edge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepr.R
import com.example.keepr.ui.theme.KeeprOnPrimary // imports the colors
import com.example.keepr.ui.theme.KeeprPrimary // imports the colors
import com.example.keepr.ui.theme.KeeprMedium // imports the colors
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon // // imports the icon package
import androidx.compose.material.icons.Icons // imports the icons
import androidx.compose.material.icons.outlined.PhotoCamera // imports the camera icon
import androidx.compose.material.icons.outlined.Badge // imports the badge icon
import androidx.compose.material.icons.outlined.Translate // imports the translate icon
import androidx.compose.material.icons.outlined.Logout // imports the logout icon
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.example.keepr.ui.components.ChangeLanguageDialog

@Composable
fun ProfileScreen(onLogout: () -> Unit){
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    
    val vm: ProfileViewModel = viewModel()
    val state by vm.state.collectAsState()
    val user = state.user


    // We make the main container for the page, this is the green background with two tone
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bottom layer with the main green color
        Box(
            modifier = Modifier
            .fillMaxSize()
            .background(KeeprPrimary)
        )

        // Top layer with a darker color
        val splitHeight = 180.dp // to split the line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(splitHeight)
                .background(KeeprMedium.copy(alpha = 0.6f)) // slightly darker shade
        )

        // We make the column for the profile
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp) // gives the padding
                .align(Alignment.TopCenter), // placement
            horizontalAlignment = Alignment.CenterHorizontally // placement
        ) {

            // The actual title for the page "profile"
            Text(
                text = stringResource(R.string.profile_title), // tittle
                color = KeeprOnPrimary, // prdefined color
                fontSize = 20.sp, // font color
                fontWeight = FontWeight.SemiBold // make it bold
            )

            Spacer(Modifier.height(25.dp)) // gives space between image anf title

            // This is the profile picture part
            Image(
                painter = painterResource(id = R.drawable.sharu), // image hardcoded for now
                contentDescription = stringResource(R.string.change_profile_picture), // tells that this is a profile picture for accebility
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(175.dp) // image size
                    .clip(RoundedCornerShape(16.dp)) // rounded edges
            )

            Spacer(Modifier.height(25.dp)) // Gives us new space

            // The name text
            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "",
                color = KeeprOnPrimary, // white text
                fontSize = 28.sp, // gives the size of the name
                fontWeight = FontWeight.Bold // gives the name bold text
            )

            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.collections_count, state.collectionCount) // shows the collction
                color = KeeprOnPrimary.copy(alpha = 0.8f), // lighter white color
                fontSize =  18.sp // Smaller size on the font
            )

            Spacer(Modifier.height(24.dp)) // space before the card

            Card( // this is the backgorund card
                shape = RoundedCornerShape(22.dp), // rounded corners
                colors = CardDefaults.cardColors(
                    containerColor = KeeprMedium.copy(alpha = 0.35f) // this gives it a tint
                ),
                modifier = Modifier
                    .fillMaxWidth() // this wil fill the screen
                    .padding(horizontal = 10.dp) // gives space on the edges
            ) {
                // For now it's empty, but we'll fill it in the next step
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_title),
                        color = KeeprOnPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(8.dp)) // gives space above and under

                    //The diffrent buttons we have on top of the box

                    // change profile picture row and icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .clickable {}, // have to code
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.change_profile_picture),
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        //Icon for profile picture
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = stringResource(R.string.change_profile_picture),
                            tint = KeeprOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Divider(color = KeeprOnPrimary.copy(alpha = 0.2f), thickness = 1.dp) // line between the buttons

                    // Change name row and icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .clickable {}, // have to code
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.change_name),
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        //Icon for change name
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = stringResource(R.string.change_name),
                            tint = KeeprOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Divider(color = KeeprOnPrimary.copy(alpha = 0.2f), thickness = 1.dp) // line between the buttons

                    // Change language row and icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .clickable { showLanguageDialog = true}, // have to code
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.change_language),
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = stringResource(R.string.change_language),
                            tint = KeeprOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Divider(color = KeeprOnPrimary.copy(alpha = 0.2f), thickness = 1.dp)

                    // Log out row and icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .clickable { onLogout() }, // calls the logout function
                        verticalAlignment = Alignment.CenterVertically
                        
                    ){
                        Text(
                            text = stringResource(R.string.log_out),
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = stringResource(R.string.log_out),
                            tint = KeeprOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (showLanguageDialog) {
                        ChangeLanguageDialog(onDismiss = { showLanguageDialog = false })
                    }
                }
            }

            Spacer(Modifier.height(40.dp)) // gives us space over and under

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(KeeprMedium.copy(alpha = 0.25f))
                    .clickable {}
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                // The text for the delete button
                Text(
                    text = stringResource(R.string.delete_user),
                    color = androidx.compose.ui.graphics.Color(0xFFFF6B6B),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
