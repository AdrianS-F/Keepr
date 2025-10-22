package com.example.keepr.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateColorAsState
import com.example.keepr.R

@Composable
fun ProfileScreen(
    onLogout: () -> Unit // 👈 NY
) {
    val greenDark = colorResource(R.color.keepr_primary) // #1A4A47
    val greenMid  = Color(0xFF537D79)
    val greenLight = Color(0xFFB5E0BD)
    val headerHeight = 120.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(greenDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = headerHeight, bottom = 24.dp)
        ) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text("Sharu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text("2 samlinger", color = Color(0xFFE6E6E6))
                }
                Divider(color = Color.White.copy(alpha = 0.12f))
            }

            val labels = listOf(
                "Endre profilbilde",
                "Endre navn",
                "Endre språk",
                "Logg ut",
                "Slett bruker"
            )

            items(labels.size) { idx ->
                val label = labels[idx]

                ProfileOptionRow(
                    text = label,
                    normalBg = Color.Transparent,
                    pressedBg = greenMid.copy(alpha = 0.35f),
                    textColor = if (label == "Slett bruker") Color(0xFFFFC7C7) else Color.White,
                    onClick = {
                        when (label) {
                            "Logg ut" -> onLogout() // 👈 KALLER callbacken
                            // TODO: håndter andre valg senere
                        }
                    }
                )

                Divider(color = Color.White.copy(alpha = 0.08f))
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .align(Alignment.TopCenter),
            color = greenDark,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Profil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    text: String,
    normalBg: Color,
    pressedBg: Color,
    textColor: Color,
    onClick: (() -> Unit)? = null // 👈 NY
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed = interaction.collectIsPressedAsState()
    val bg = animateColorAsState(if (pressed.value) pressedBg else normalBg, label = "rowBg")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(bg.value, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick?.invoke() } // 👈 NY
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = textColor)
    }
}
