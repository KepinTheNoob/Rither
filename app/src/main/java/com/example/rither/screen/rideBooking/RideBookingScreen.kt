package com.example.rither.screen.rideBooking

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RideBookingScreen(navController: NavController) {
    var pickupName by remember { mutableStateOf("") }
    var dropoffName by remember { mutableStateOf("") }
    var pickupLocation by remember { mutableStateOf<LatLng?>(null) }
    var dropoffLocation by remember { mutableStateOf<LatLng?>(null) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    // --- Permission ---
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // --- Fetch current location ---
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(Modifier.fillMaxSize()) {
        // --- Map Area ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (locationPermissionState.status.isGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    currentLocation?.let {
                        Marker(state = MarkerState(it), title = "You are here")
                    }
                    pickupLocation?.let {
                        Marker(state = MarkerState(it), title = "Pickup")
                    }
                    dropoffLocation?.let {
                        Marker(state = MarkerState(it), title = "Drop-off")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Location permission required")
                }
            }

            // Back Button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            // My Location Button
            IconButton(
                onClick = {
                    currentLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = "My Location")
            }
        }

        // --- Booking Sheet ---
        BookingSheet(
            pickupName = pickupName,
            onPickupNameChange = { pickupName = it },
            onPickupSelected = { name, latLng ->
                pickupName = name
                pickupLocation = latLng
            },
            dropoffName = dropoffName,
            onDropoffNameChange = { dropoffName = it },
            onDropoffSelected = { name, latLng ->
                dropoffName = name
                dropoffLocation = latLng
            }
        )
    }
}

@Composable
fun BookingSheet(
    pickupName: String,
    onPickupNameChange: (String) -> Unit,
    onPickupSelected: (String, LatLng) -> Unit,
    dropoffName: String,
    onDropoffNameChange: (String) -> Unit,
    onDropoffSelected: (String, LatLng) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        Text(
            text = "Where to?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))

        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Circle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                DottedDivider()
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.width(16.dp))

            Column {
                LocationSearchBar(
                    label = "Pick-up location",
                    query = pickupName,
                    onQueryChange = onPickupNameChange,
                    onPlaceSelected = onPickupSelected
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                LocationSearchBar(
                    label = "Drop-off location",
                    query = dropoffName,
                    onQueryChange = onDropoffNameChange,
                    onPlaceSelected = onDropoffSelected
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { /* Handle Next */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LocationSearchBar(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onPlaceSelected: (String, LatLng) -> Unit
) {
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    // Track the current query and debounce it
    LaunchedEffect(query) {
        // Wait 500ms after the last keystroke before running
        kotlinx.coroutines.delay(500)

        if (query.length > 2) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    predictions = response.autocompletePredictions
                }
                .addOnFailureListener {
                    predictions = emptyList()
                }
        } else {
            predictions = emptyList()
        }
    }

    Column {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            items(predictions) { prediction ->
                Text(
                    text = prediction.getFullText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val placeId = prediction.placeId
                            val placeRequest = FetchPlaceRequest.builder(
                                placeId,
                                listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                            ).build()
                            placesClient.fetchPlace(placeRequest)
                                .addOnSuccessListener { result ->
                                    result.place.latLng?.let { latLng ->
                                        onPlaceSelected(result.place.name ?: "", latLng)
                                        predictions = emptyList()
                                    }
                                }
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun DottedDivider() {
    Column(
        modifier = Modifier.height(30.dp).padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(5) {
            Box(
                modifier = Modifier.size(2.dp).background(Color.Gray, shape = CircleShape)
            )
        }
    }
}
