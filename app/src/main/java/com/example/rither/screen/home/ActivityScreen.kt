package com.example.rither.screen.home

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rither.data.Screen
import com.example.rither.data.model.Ride
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavController,
    activityViewModel: ActivityViewModel = viewModel()
) {
    var selectedMainTab by remember { mutableStateOf("History") }

    Scaffold(
        topBar = { ActivityTopBar() },
        bottomBar = {
            HomeBottomBar(
                selectedItem = "Rides",
                onItemSelected = { itemName ->
                    if (itemName == "Home") {
                        navController.navigate(Screen.Home.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ActivityScreenContent(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            selectedMainTab = selectedMainTab,
            onMainTabSelected = { selectedMainTab = it },
            activityViewModel = activityViewModel
        )
    }
}

@Composable
fun ActivityTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Activity",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActivityScreenContent(
    modifier: Modifier = Modifier,
    selectedMainTab: String,
    onMainTabSelected: (String) -> Unit,
    activityViewModel: ActivityViewModel
) {
    val historyFilters = listOf("All", "Scooter", "Car")
    val reservationFilters = listOf("All", "Scooter", "Car", "Upcoming", "Assigned")

    var selectedHistoryFilter by remember { mutableStateOf(historyFilters.first()) }
    var selectedReservationFilter by remember { mutableStateOf(reservationFilters[3]) }

    val isLoading by activityViewModel.isLoading.collectAsStateWithLifecycle()
    val rideHistoryItems by activityViewModel.rideHistory.collectAsStateWithLifecycle()
    val rideReservationItems by activityViewModel.rideHistory.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        activityViewModel.loadRideHistory()
    }

    val filteredRideHistory = remember(rideHistoryItems, selectedHistoryFilter) {
        val completed = rideHistoryItems.filter { it.status.equals("complete", ignoreCase = true) }

        when (selectedHistoryFilter) {
            "All" -> completed
            "Scooter" -> completed.filter { it.rideType.equals("scooter", ignoreCase = true) }
            "Car" -> completed.filter { it.rideType.equals("car", ignoreCase = true) }
            else -> completed
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MainTabRow(
                selectedTab = selectedMainTab,
                onTabSelected = onMainTabSelected
            )
        }

        item {
            when (selectedMainTab) {
                "History" -> FilterChipRow(
                    filters = historyFilters,
                    selectedFilter = selectedHistoryFilter,
                    onFilterSelected = { selectedHistoryFilter = it }
                )
                "Reservations" -> FilterChipRow(
                    filters = reservationFilters,
                    selectedFilter = selectedReservationFilter,
                    onFilterSelected = { selectedReservationFilter = it }
                )
            }
        }

        when (selectedMainTab) {
            "History" -> {
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (filteredRideHistory.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Show a more helpful message
                            val message = if (selectedHistoryFilter == "All") "You have no ride history." else "No $selectedHistoryFilter rides found."
                            Text(message, color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredRideHistory) { ride ->
                        RideHistoryItem(ride = ride, activityViewModel = activityViewModel)
                    }
                }
            }
            "Reservations" -> {
                item {
                    ReservationsContent(
                        filter = selectedReservationFilter, activityViewModel = activityViewModel
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun MainTabRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("History", "Reservations")
    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)]),
                height = 3.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        divider = {
            Divider(color = Color.Gray.copy(alpha = 0.3f))
        }
    ) {
        tabs.forEach { title ->
            Tab(
                selected = selectedTab == title,
                onClick = { onTabSelected(title) },
                text = {
                    Text(
                        title,
                        fontWeight = if (selectedTab == title) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.Transparent,
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = true
                )
            )
        }
    }
}

@Composable
fun RideHistoryItem(ride: Ride, activityViewModel: ActivityViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        var driverName by remember { mutableStateOf<String?>(null)}

        LaunchedEffect(Unit) {
            driverName = activityViewModel.getDriverName(ride.driverId.toString())
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = ride.createdAt.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                RideRouteInfo(
                    start = ride.pickUpAddress,
                    end = ride.dropOffAddress
                )

                Text(
                    text = "Driver: $driverName",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Rp ${formatRupiah(ride.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ride.rideType,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* TODO: Book Again */ },
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
fun RideRouteInfo(start: String, end: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Circle,
                contentDescription = "Start",
                tint = Color.Blue,
                modifier = Modifier.size(12.dp)
            )
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp), color = Color.Gray
            )
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "End",
                tint = Color.Blue,
                modifier = Modifier.size(14.dp)
            )
        }

        // Locations
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = end,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ReservationsContent(
    filter: String,
    activityViewModel: ActivityViewModel
) {
    val rides = remember(filter, activityViewModel.rideHistory.collectAsState().value) {
        val base = activityViewModel.rideHistory.value
            .filter {
                it.passengerId.contains(activityViewModel.getCurrentUserId()) &&
                        !it.status.equals("complete", ignoreCase = true)
            }

        when (filter) {
            "All" -> base

            "Scooter" -> base.filter {
                it.rideType.equals("scooter", ignoreCase = true)
            }

            "Car" -> base.filter {
                it.rideType.equals("car", ignoreCase = true)
            }

            "Upcoming" -> base.filter {
                it.status.equals("pending", ignoreCase = true)
            }

            "Assigned" -> base.filter {
                it.status.equals("assigned", ignoreCase = true)
            }

            else -> emptyList()
        }
    }

    if (filter.isNotEmpty()) {
        if (rides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming rides.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rides) { ride ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        var driverName by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(Unit) {
                            driverName = activityViewModel.getDriverName(ride.driverId.toString())
                        }

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = ride.createdAt.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                RideRouteInfo(
                                    start = ride.pickUpAddress,
                                    end = ride.dropOffAddress
                                )

                                Text(
                                    text = "Driver: $driverName",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.height(IntrinsicSize.Min)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rp ${formatRupiah(ride.price)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ride.rideType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Button(
                                    onClick = {
                                        activityViewModel.cancelReservation(
                                            rideId = ride.id ?: "",
                                            onSuccess = {
                                                println("DEBUG → Reservation cancelled, reloading history")
                                                activityViewModel.loadRideHistory()
                                            },
                                            onFailure = { e ->
                                                println("❌ Cancel failed → ${e.message}")
                                            }
                                        )
                                    },
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Cancel", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No items for filter: $filter",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}



fun formatRupiah(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return formatter.format(amount)
}