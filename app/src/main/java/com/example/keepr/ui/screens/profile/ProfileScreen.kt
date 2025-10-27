package com.example.keepr.ui.screens.profile // screen for profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keepr.R
import com.example.keepr.ui.theme.KeeprOnPrimary
import com.example.keepr.ui.theme.KeeprPrimary
import com.example.keepr.ui.theme.KeeprMedium
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Divider



@Composable
fun ProfileScreen(onLogout: () -> Unit){

    // We make a hardcoded name for now.
    val name = "Sharu" // Name of the profile
    val collectionsCount = 2 // hardcoded for now

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
                text = "Profile", // tittle
                color = KeeprOnPrimary, // prdefined color
                fontSize = 20.sp, // font color
                fontWeight = FontWeight.SemiBold // make it bold
            )

            Spacer(Modifier.height(25.dp)) // gives space between image anf title

            // This is the profile picture part
            Image(
                painter = painterResource(id = R.drawable.sharu), // image hardcoded for now
                contentDescription = "Profile picture", // tells that this is a profile picture for accebility
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(175.dp) // image size
                    .clip(RoundedCornerShape(16.dp)) // rounded edges
            )

            Spacer(Modifier.height(25.dp)) // Gives us new space

            // The name text
            Text(
                text = name, // the name we made earlier
                color = KeeprOnPrimary, // white text
                fontSize = 28.sp, // gives the size of the name
                fontWeight = FontWeight.Bold // gives the name bold text
            )

            Spacer(Modifier.height(6.dp))
            Text(
                text = "$collectionsCount Collections", // shows the collction
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
                        text = "Profile",
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
                            text = "Change profile picture",
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        //Icon for profile picture
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Profile icon",
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
                            text = "Change name",
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        //Icon for change name
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = "Name icon",
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
                            .clickable {}, // have to code
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Change language",
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = "Language icon",
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
                            text = "Log out",
                            color = KeeprOnPrimary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout icon",
                            tint = KeeprOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
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
                    text = "Delete user",
                    color = androidx.compose.ui.graphics.Color(0xFFFF6B6B),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
/*
@Composable
private fun CardRow( // Function for the card
    text: String, // we tell that it is a string
    onClick: () -> Unit = {} // this is a onclick
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .clickable { onClick()},
        verticalAlignment = Alignment.CenterVertically // tells that is is going verticaly
    ) {
        Text(
            text = text, // tells that it is a text
            color = KeeprOnPrimary, // keeps the color to match
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }

    // This gives us the lines between the buttons
    androidx.compose.material3.Divider( // gives us the divider
        color = KeeprOnPrimary.copy(alpha = 0.2f), // changes the color
        thickness = 1.dp // the height on the line
    )

    Spacer(Modifier.height(4.dp)) // gives us new space for above and under
}

*/ // denne brukte jeg før jeg hadde ikoner, vet ikke helt ennå om jeg skal fjerne 