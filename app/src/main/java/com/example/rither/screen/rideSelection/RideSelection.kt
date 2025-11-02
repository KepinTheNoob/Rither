package com.example.rither.screen.rideSelection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RideSelectionScreen() {
    var selectedRide by remember { mutableStateOf("Scooter") }

    Column(Modifier.fillMaxSize()) {
        // --- Map Area ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Dummy Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0)), // Lighter gray
                contentAlignment = Alignment.Center
            ) {
                Text("Map Placeholder", color = Color.DarkGray)
            }

            // Custom Top Bar (Overlay)
            RideSelectionTopBar(
                onBackClick = { /*TODO*/ },
                onEditClick = { /*TODO*/ }
            )

            // "18 min" Tag
            Card(
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp, bottom = 40.dp) // Offset
            ) {
                Text(
                    text = "18 min",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            // My Location Button
            IconButton(
                onClick = { /* TODO: Center map */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = "My Location")
            }
        }

        // --- Ride Selection Sheet Area ---
        RideSelectionSheet(
            selectedRide = selectedRide,
            onRideSelected = { selectedRide = it }
        )
    }
}

@Composable
fun RideSelectionTopBar(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(0.dp), // Full width
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            // Route Info
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Route Visualizer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = "Pick up",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(10.dp)
                    )
                    // Simple line
                    Box(modifier = Modifier.height(18.dp).width(1.dp).background(Color.Gray))
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Drop off",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }

                // Locations
                Column {
                    Text(
                        text = "BINUS Anggrek Campus Basement",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "House of Mysteries 60 No. 283A",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            TextButton(onClick = onEditClick) {
                Text("Edit")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideSelectionSheet(
    selectedRide: String,
    onRideSelected: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Search", "Reserve")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 16.dp) // Padding for tabs
    ) {
        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Ride Options (assuming 'Search' tab is selected)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RideOptionItem(
                title = "Scooter",
                icon = Icons.Default.TwoWheeler,
                passengerInfo = "1",
                timeInfo = "18 Minutes",
                price = "15.000",
                isSelected = selectedRide == "Scooter",
                onClick = { onRideSelected("Scooter") }
            )
            RideOptionItem(
                title = "Car",
                icon = Icons.Default.DirectionsCar,
                passengerInfo = "4 - 6",
                timeInfo = "24 Minutes",
                price = "23.000",
                isSelected = selectedRide == "Car",
                onClick = { onRideSelected("Car") }
            )

            Spacer(Modifier.height(8.dp))

            // Request Button
            Button(
                onClick = { /* TODO: Handle request */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Request Ride", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideOptionItem(
    title: String,
    icon: ImageVector,
    passengerInfo: String,
    timeInfo: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Person, contentDescription = "Passengers", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Text(passengerInfo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("•", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(timeInfo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Text("Rp", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Top))
            Spacer(Modifier.width(4.dp))
            Text(price, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}