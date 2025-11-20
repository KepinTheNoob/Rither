package com.example.rither.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rither.data.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = { ProfileTopBar(navController) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        ProfileScreenContent(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            profileViewModel = profileViewModel,
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    navController: NavController
) {
    TopAppBar(
        title = { Text("Profile") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent // Make transparent to see header
        )
    )
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    profileViewModel: ProfileViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var isDarkMode by remember { mutableStateOf(isDarkTheme) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header with Gradient and Profile Icon
        item {
            ProfileHeader()
        }

        // 2. Spacer for the overlapping icon
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        // 3. User Info Section
        item {
            UserInfoSection(
                name = "Kevin Setiawan",
                email = "kevin.setiawan@gmail.com",
                phone = "+62 8213232423",
                onEditClick = { /* TODO: Handle edit */ }
            )
        }

        // 4. Divider
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
        }

        // 5. Google Connected Item
        item {
            GoogleLinkItem(
                isConnected = true,
                onClick = { /* TODO: Handle Google link */ }
            )
        }

        // 6. Account Section
        item { SectionHeader("Account") }
        item {
            ProfileMenuItem(
                icon = Icons.Default.Security,
                title = "Account security",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.Payment,
                title = "Payment",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.DirectionsCar,
                title = "Be a driver",
                onClick = { /* TODO: Navigate */ }
            )
        }

        // 7. Preferences Section
        item { SectionHeader("Preferences") }
        item {
            ProfileMenuSwitchItem(
                icon = Icons.Default.ModeNight,
                title = "Dark mode",
                checked = isDarkMode,
                onCheckedChange = {
                    isDarkMode = it
                    onThemeChange(it)
                }

            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.Language,
                title = "Language",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ListAlt,
                title = "Manage data",
                onClick = { /* TODO: Navigate */ }
            )
        }

        // 8. Other Section
        item { SectionHeader("Other") }
        item {
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpCenter,
                title = "Support center",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy policy",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "Data attribution",
                onClick = { /* TODO: Navigate */ }
            )
        }
        item {
            ProfileMenuItem(
                icon = Icons.Default.Delete,
                title = "Log out",
                onClick = {
                    profileViewModel.logout()
                    onThemeChange(false)
                    navController.navigate(Screen.Login.name) {
                        popUpTo("home") { inclusive = true }
                    } }
            )
        }

        // 9. Bottom Spacer
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * The header with the gradient background and overlapping profile icon.
 */
@Composable
fun ProfileHeader() {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF63B8FF), Color(0xFF42A5F5), Color(0xFF1E88E5))
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp) // Height of the gradient area
                .background(gradient)
        )

        // Overlapping Profile Icon
        Box(
            modifier = Modifier
                .padding(top = 90.dp) // Pushes icon down (140dp - 50dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp) // Optional: for a border effect
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(60.dp),
                tint = Color.Gray.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * The section showing user's name, email, phone, and an edit button.
 */
@Composable
fun UserInfoSection(
    name: String,
    email: String,
    phone: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.ModeEdit,
                contentDescription = "Edit Profile",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * The special item for linking the Google account.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleLinkItem(
    isConnected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Google 'G' Logo
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)), // Gray background
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "G",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF4285F4) // Google Blue
                )
            }

            Text(
                text = "Google",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            if (isConnected) {
                Text(
                    text = "Connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Connected",
                    tint = Color.Green.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * A small gray header for a section.
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(top = 8.dp)
    )
}

/**
 * A standard clickable menu item with an icon, title, and trailing content.
 */
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit = { ArrowIcon() }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        trailingContent()
    }
}

/**
 * A menu item with a Switch.
 */
@Composable
fun ProfileMenuSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ProfileMenuItem(
        icon = icon,
        title = title,
        onClick = { onCheckedChange(!checked) }, // Click row to toggle
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

/**
 * A simple arrow icon.
 */
@Composable
private fun ArrowIcon() {
    Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = Color.Gray
    )
}