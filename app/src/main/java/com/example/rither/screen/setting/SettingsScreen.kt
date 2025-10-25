package com.example.rither.screen.setting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Dummy biar navController bisa keterima
import com.example.rither.ui.theme.RitherTheme

@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))

            ThemeSettingCard(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrivacyPolicyCard()
        }
    }
}

@Composable
fun ThemeSettingCard(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Light / Dark",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ThemeToggleButton(
                isDarkTheme = isDarkTheme,
                onToggle = { onThemeChange(!isDarkTheme) }
            )
        }
    }
}

@Composable
fun PrivacyPolicyCard() {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Privacy Policy & Terms",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Read our policies",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ThemeToggleButton(isDarkTheme: Boolean, onToggle: () -> Unit) {
    val horizontalBias by animateFloatAsState(
        targetValue = if (isDarkTheme) 1f else -1f,
        label = "ThemeToggleBias"
    )

    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onToggle),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Light",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (!isDarkTheme) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Dark",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isDarkTheme) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .align(BiasAlignment(horizontalBias = horizontalBias, verticalBias = 0f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )

                Text(
                    text = if (isDarkTheme) "Dark" else "Light",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

//@Preview(showBackground = true, name = "Light Mode")
//@Composable
//fun SettingsScreenLightPreview() {
//    RitherTheme(darkTheme = false) {
//        // Buat NavController dummy untuk kebutuhan preview
//        val dummyNavController = rememberNavController()
//        SettingsScreen(navController = dummyNavController)
////        SettingsScreen(navController: NavController) // -> Ini yang lawas
//    }
//}
//
//@Preview(showBackground = true, name = "Dark Mode")
//@Composable
//fun SettingsScreenDarkPreview() {
//    RitherTheme(darkTheme = true) {
//        // Buat NavController palsu (dummy) juga di sini
//        val dummyNavController = rememberNavController()
//        SettingsScreen(navController = dummyNavController)
////        SettingsScreen() // -> Ini yang lawas
//    }
//}
