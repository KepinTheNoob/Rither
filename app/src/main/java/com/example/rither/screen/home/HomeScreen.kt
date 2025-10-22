package com.example.rither.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.rither.data.Screen
import com.example.rither.screen.myTrips.MyTripsScreen
import com.example.rither.screen.offerRide.OfferRideScreen
import com.example.rither.screen.signup.ProfileViewModel
import com.example.rither.ui.theme.RitherTheme

@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val userName by profileViewModel.userName.observeAsState("User") // Observe user name
    val imageUrl by profileViewModel.imageUrl.observeAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                TopBarSection(
                    navController = navController,
                    userName = userName,
                    imageUrl = imageUrl
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Pass the selected tab + a callback to RideTypeSelector
                RideTypeSelector(
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                when (selectedTab) {
                    0 -> FindRideSection()
                    1 -> OfferRideSection()
                    2 -> MyTripsSection()
                }
            }
//            item {
//                RideInfoCard(
//                    driverInitial = "A",
//                    driverName = "Amira",
//                    rating = 4.8f,
//                    origin = "Kemang",
//                    destination = "Senayan",
//                    carModel = "Toyota Yaris",
//                    rideTime = "Today, 5:30 PM",
//                    price = "$4.50",
//                    seatsAvailable = 2,
//                    initialBackgroundColor = Color(0xFFE0E0E0)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//            item {
//                RideInfoCard(
//                    driverInitial = "R",
//                    driverName = "Rizky",
//                    rating = 4.7f,
//                    origin = "BSD",
//                    destination = "Kuningan",
//                    carModel = "Honda Jazz",
//                    rideTime = "Today, 6:15 PM",
//                    price = "$5.80",
//                    seatsAvailable = 1,
//                    initialBackgroundColor = Color(0xFFD1C4E9)
//                )
//            }
        }
    }
}

@Composable
fun TopBarSection(
    navController: NavController,
    userName: String?,
    imageUrl: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
            ),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { navController.navigate(Screen.Profile.name) }, // Navigate to profile on click
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Hi Andrew!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { navController.navigate(Screen.Setting.name) }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RideTypeSelector(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val items = listOf("Find Ride", "Offer Ride", "My Trips")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, text ->
            val isSelected = selectedIndex == index
            Button(
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50),
                colors = if (isSelected) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                } else {
                    ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                },
                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null
            ) {
                Text(
                    text = text,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchDestinationField() {
    var searchText by remember { mutableStateOf("") }
    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search destination") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun MapPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Map Location Icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Map preview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
fun RideInfoCard(
    driverInitial: String,
    driverName: String,
    rating: Float,
    origin: String,
    destination: String,
    carModel: String,
    rideTime: String,
    price: String,
    seatsAvailable: Int,
    initialBackgroundColor: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(initialBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = driverInitial,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = driverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Rating Icon",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = carModel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    InfoRow(icon = Icons.Default.LocationOn, text = origin)
                    Spacer(modifier = Modifier.height(4.dp))
                    InfoRow(icon = Icons.AutoMirrored.Filled.ArrowForward, text = destination)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = rideTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = price,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = if (seatsAvailable > 1) "$seatsAvailable seats" else "$seatsAvailable seat",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FindRideSection() {
    SearchDestinationField()
    Spacer(modifier = Modifier.height(16.dp))
    MapPreviewCard()
    Spacer(modifier = Modifier.height(24.dp))
    RideInfoCard(
        driverInitial = "A",
        driverName = "Amira",
        rating = 4.8f,
        origin = "Kemang",
        destination = "Senayan",
        carModel = "Toyota Yaris",
        rideTime = "Today, 5:30 PM",
        price = "$4.50",
        seatsAvailable = 2,
        initialBackgroundColor = Color(0xFFE0E0E0)
    )
    Spacer(modifier = Modifier.height(16.dp))

    RideInfoCard(
        driverInitial = "R",
        driverName = "Rizky",
        rating = 4.7f,
        origin = "BSD",
        destination = "Kuningan",
        carModel = "Honda Jazz",
        rideTime = "Today, 6:15 PM",
        price = "$5.80",
        seatsAvailable = 1,
        initialBackgroundColor = Color(0xFFD1C4E9)
    )
}

@Composable
fun OfferRideSection() {
    OfferRideScreen()
}

@Composable
fun MyTripsSection() {
    MyTripsScreen()
}


@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun RidesScreenPreview() {
    RitherTheme {
        HomeScreen(navController = rememberNavController())
    }
}
