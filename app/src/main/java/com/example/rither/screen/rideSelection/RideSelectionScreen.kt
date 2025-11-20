package com.example.rither.screen.rideSelection

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun RideSelectionScreen(
    navController: NavController,
    pickupName: String,
    dropoffName: String,
    pickupLatLng: LatLng,
    dropoffLatLng: LatLng,
    rideSelectionViewModel: RideSelectionViewModel = viewModel()
) {
    var selectedRide by remember { mutableStateOf("Scooter") }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pickupLatLng, 13f)
    }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var boundsToAnimate by remember { mutableStateOf<LatLngBounds?>(null) }

    // Launch fetch once whenever pickup/dropoff changes.
    LaunchedEffect(pickupLatLng, dropoffLatLng) {
        // Use ViewModel's callback-style getRoutePoints (non-blocking)
        rideSelectionViewModel.getRoutePoints(pickupLatLng, dropoffLatLng) { polyline ->
            routePoints = polyline

            // compute bounds for camera animation if we have points (fallback to include start/end)
            val builder = LatLngBounds.builder()
            builder.include(pickupLatLng)
            builder.include(dropoffLatLng)
            for (p in polyline) builder.include(p)
            boundsToAnimate = try {
                builder.build()
            } catch (e: Exception) {
                null
            }
        }
    }

    LaunchedEffect(boundsToAnimate) {
        boundsToAnimate?.let { bounds ->
            // animate camera to bounds (wrap in try-catch because camera update may throw if map not ready)
            try {
                cameraPositionState.animate(
                    update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, 150),
                    durationMs = 1200
                )
            } catch (_: Exception) {
                // ignore animation errors (map not ready, etc.)
            }
        }
    }

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
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false)
                ) {
                    // Markers
                    Marker(
                        state = MarkerState(pickupLatLng),
                        title = "Pickup: $pickupName",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                    Marker(
                        state = MarkerState(dropoffLatLng),
                        title = "Drop-off: $dropoffName",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )

                    // Route line
                    if (routePoints.isNotEmpty()) {
                        Polyline(
                            points = routePoints,
                            color = androidx.compose.ui.graphics.Color.Blue,
                            width = 10f
                        )
                    }
                }
            }

            // Custom Top Bar (Overlay)
            RideSelectionTopBar(
                navController = navController,
                onBackClick = { /* TODO: popBackStack() */ },
                onEditClick = { /* TODO: navigate back */ },
                pickupName = pickupName,
                dropoffName = dropoffName
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
                    text = rideSelectionViewModel.lastRouteDurationText.value.ifBlank { "..." },
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
            onRideSelected = { selectedRide = it },
            pickupName = pickupName,
            dropoffName = dropoffName,
            pickupLatLng = pickupLatLng,
            dropoffLatLng = dropoffLatLng,
            navController = navController,
            rideSelectionViewModel
        )
    }
}

@Composable
fun RideSelectionTopBar(
    navController: NavController,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    pickupName: String,
    dropoffName: String
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
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
                        text = pickupName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = dropoffName,
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
    onRideSelected: (String) -> Unit,
    pickupName: String,
    dropoffName: String,
    pickupLatLng: LatLng,
    dropoffLatLng: LatLng,
    navController: NavController,
    rideSelectionViewModel: RideSelectionViewModel
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Search", "Reserve")
    val distanceKm = calculateDistanceInKm(pickupLatLng, dropoffLatLng)
    val scooterPrice = calculatePrice(distanceKm, "Scooter")
    val carPrice = calculatePrice(distanceKm, "Car")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 16.dp)
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
                price = scooterPrice.toString(),
                isSelected = selectedRide == "Scooter",
                onClick = { onRideSelected("Scooter") }
            )
            RideOptionItem(
                title = "Car",
                icon = Icons.Default.DirectionsCar,
                passengerInfo = "4 - 6",
                timeInfo = "24 Minutes",
                price = carPrice.toString(),
                isSelected = selectedRide == "Car",
                onClick = { onRideSelected("Car") }
            )

            Spacer(Modifier.height(8.dp))

            // Request Button
            Button(
                onClick = {
                    Log.d("RideSelection", "Request button clicked")
                    rideSelectionViewModel.requestRide(
                        pickupName = pickupName,
                        dropoffName = dropoffName,
                        pickupLatLng = pickupLatLng,
                        dropoffLatLng = dropoffLatLng,
                        selectedRide = selectedRide,
                        distance = (calculateDistanceInKm(pickupLatLng, dropoffLatLng) * 1000).toInt(),
                        durationText = rideSelectionViewModel.lastRouteDurationText.value,
                        price = if (selectedRide == "Scooter") scooterPrice else carPrice,
                        navController = navController
                    )
                },
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

fun calculateDistanceInKm(start: LatLng, end: LatLng): Double {
    val earthRadius = 6371 // KM

    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLng = Math.toRadians(end.longitude - start.longitude)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(start.latitude)) *
            Math.cos(Math.toRadians(end.latitude)) *
            Math.sin(dLng / 2) *
            Math.sin(dLng / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return earthRadius * c
}

fun calculatePrice(distance: Double, rideType: String): Int {
    return when (rideType) {
        "Scooter" -> (((distance * 1500).toInt() + 999) / 1000) * 1000
        "Car" -> (((distance * 1500).toInt() + 999) / 1000) * 1000
        else -> 0
    }
}

