package com.example.rither.screen.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.rither.data.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    var selectedBottomItem by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = { HomeTopBar(navController) },
        bottomBar = {
            HomeBottomBar(
                selectedItem = selectedBottomItem,
                onItemSelected = { selectedBottomItem = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        HomeScreenContent(
            homeViewModel,
            navController,
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun HomeScreenContent(
    homeViewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Greeting(homeViewModel = homeViewModel)
            Spacer(Modifier.height(16.dp))
        }

        item {
            LatestRideCard()
        }

        item {
            SectionHeader("Rides")
        }

        item {
            RideActionCard(
                navController,
                title = "Order Now",
                subtitle = "Order a ride to your location",
                icon = Icons.Default.DirectionsCar,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = Color(0xFF0065FF),
                onClick = { navController.navigate(Screen.RideBooking.name) }
            )
        }

        item {
            RideActionCard(
                navController,
                title = "Reserve",
                subtitle = "Reserve rides on specific dates",
                icon = Icons.Default.CalendarToday,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconColor = Color(0xFF008A5F),
                onClick = { navController.navigate(Screen.RideSelection.name) }
            )
        }

        item {
            SectionHeader("Explore")
        }

        item {
            ExploreCard(
                title = "24/7 Support",
                subtitle = "Get support anywhere, anytime",
                // TODO: Replace with actual image
                placeholderColor = Color.DarkGray,
                onClick = {}
            )
        }

        item {
            ExploreCard(
                title = "Be a Driver!",
                subtitle = "Sign up if you have a car or scooter",
                // TODO: Replace with actual image
                placeholderColor = Color.Gray,
                onClick = { navController.navigate(Screen.DriverOnboarding.name) }
            )
        }

        item {
            Spacer(Modifier.height(16.dp)) // Extra space at the bottom
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Destination...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            singleLine = true
        )

        // Notification Icon
        BadgedBox(
            badge = {
                Badge(
                    modifier = Modifier.offset(x = (-6).dp, y = 6.dp),
                    containerColor = Color.Red
                )
            }
        ) {
            IconButton(
                onClick = { navController.navigate(Screen.Notifications.name) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(elevation = 2.dp, shape = CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }

        // Profile Icon
        IconButton(
            onClick = { navController.navigate(Screen.Profile.name) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.shadow(elevation = 2.dp, shape = CircleShape)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
        }
    }
}

@Composable
fun Greeting(
    homeViewModel: HomeViewModel
) {
    var userName by remember { mutableStateOf<String?>(null)}

    LaunchedEffect(Unit) {
        userName = homeViewModel.getUserName()
    }

    Text(
        text = if (userName != null) "Hi, $userName! 👋" else "Guest",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LatestRideCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F9E0)), // Light green
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side (Route Info)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Route Visualizer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = "Start",
                        tint = Color.Blue,
                        modifier = Modifier.size(12.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp), color = Color.Gray
                    )
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "End",
                        tint = Color.Blue,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Locations & Driver
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "BINUS Anggrek Campus Basement",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "House of Mysteries 60 No. 283A",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Driver: Yogunmandr Daltae Heinmen",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Right Side (Price & Button)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(IntrinsicSize.Max) // To align button
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rp 12.000",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scooter",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* TODO */ },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Book Again", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideActionCard(
    navController: NavController,
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder for image
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                tint = iconColor
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreCard(
    title: String,
    subtitle: String,
    placeholderColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // TODO: Replace this Box with an Image Composable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(placeholderColor)
            )
            // Image(
            //     painter = painterResource(id = R.drawable.your_image),
            //     contentDescription = title,
            //     contentScale = ContentScale.Crop,
            //     modifier = Modifier.fillMaxSize()
            // )

            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )

            // Text Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = Color.White
                )
            }
        }
    }
}
@Composable
fun HomeBottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(72.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = selectedItem == "Home",
                onClick = { onItemSelected("Home") }
            )
            BottomNavItem(
                title = "Rides",
                icon = Icons.Default.ListAlt,
                isSelected = selectedItem == "Rides",
                onClick = { onItemSelected("Rides") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD6E4FF) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
    }
}
