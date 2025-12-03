package com.example.rither.screen.driver.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rither.data.model.Ride
import com.example.rither.screen.home.RideRouteInfo
import com.example.rither.screen.home.formatRupiah
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    driverDashboardViewModel: DriverDashboardViewModel = viewModel()
) {
    var selectedMainTab by remember { mutableStateOf("Request") }

    Scaffold(
        topBar = { DriverDashboardTopBar(navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        DriverDashboardScreenContent(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            selectedMainTab = selectedMainTab,
            onMainTabSelected = { selectedMainTab = it },
            driverDashboardViewModel = driverDashboardViewModel
        )
    }
}

@Composable
fun DriverDashboardTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Text(
            "Driver Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DriverDashboardScreenContent(
    modifier: Modifier = Modifier,
    selectedMainTab: String,
    onMainTabSelected: (String) -> Unit,
    driverDashboardViewModel: DriverDashboardViewModel
) {
    val historyFilters = listOf("All", "Scooter", "Car")

    var selectedHistoryFilter by remember { mutableStateOf(historyFilters.first()) }

    val isLoading by driverDashboardViewModel.isLoading.collectAsStateWithLifecycle()
    val rideHistoryItems by driverDashboardViewModel.rideHistory.collectAsStateWithLifecycle()
    val requestItems by driverDashboardViewModel.requests.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        driverDashboardViewModel.loadRequests()
        driverDashboardViewModel.loadReservations()
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
                "Request" -> FilterChipRow(
                    filters = historyFilters,
                    selectedFilter = selectedHistoryFilter,
                    onFilterSelected = { selectedHistoryFilter = it }
                )
            }
        }

        when (selectedMainTab) {
            "Request" -> {
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
                } else if (requestItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No ride requests available.", color = Color.Gray)
                        }
                    }
                } else {
                    val filteredList = when (selectedHistoryFilter) {
                        "All" -> requestItems
                        else -> requestItems.filter {
                            it.rideType.equals(
                                selectedHistoryFilter,
                                ignoreCase = true
                            )
                        }
                    }

                    if (filteredList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No requests match this ride type.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(filteredList) { ride ->
                            RequestItem(
                                ride = ride,
                                driverDashboardViewModel = driverDashboardViewModel
                            )
                        }
                    }
                }

            }
            "Reservations" -> {
                item {
                    ReservationsContent(
                        driverDashboardViewModel = driverDashboardViewModel
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
    val tabs = listOf("Request", "Reservations")
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
fun RequestItem(ride: Ride, driverDashboardViewModel: DriverDashboardViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        var driverName by remember { mutableStateOf<String?>(null)}

        LaunchedEffect(Unit) {
            driverName = driverDashboardViewModel.getDriverName(ride.driverId.toString())
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
                    onClick = { driverDashboardViewModel.acceptRideRequest(ride.id ?: "") },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Become Driver", fontSize = 12.sp)
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
    driverDashboardViewModel: DriverDashboardViewModel
) {
    val reservation by driverDashboardViewModel.driverReservation.collectAsState()

    if (reservation == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                // TODO: navigate to create reservation page
            }) {
                Text("Create Reservation")
            }
        }
    } else {
        // Driver ALREADY has a reservation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
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
                            text = reservation!!.createdAt.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        RideRouteInfo(
                            start = reservation!!.pickUpAddress,
                            end = reservation!!.dropOffAddress
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Rp ${formatRupiah(reservation!!.price)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reservation!!.rideType,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                reservation?.id?.let { rideId ->
                                    driverDashboardViewModel.cancelReservation(
                                        rideId,
                                        onSuccess = {
                                            println("DEBUG → Cancel successful")
                                        },
                                        onFailure = { e ->
                                            println("❌ Cancel failed: ${e.message}")
                                        }
                                    )
                                }
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

fun formatRupiah(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return formatter.format(amount)
}