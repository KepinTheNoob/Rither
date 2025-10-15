package com.example.rither.screen.offerRide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rither.R
import com.example.rither.ui.theme.RitherTheme
import com.example.rither.ui.theme.SkylineOnSurface
import com.example.rither.ui.theme.SkylinePrimary

@Composable
fun OfferRideScreen() {
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var people by remember { mutableIntStateOf(1) }
    var price by remember { mutableIntStateOf(1000) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Offer a Ride",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = from,
                        onValueChange = { from = it },
                        label = { Text("From") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = to,
                        onValueChange = { to = it },
                        label = { Text("To") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.clock),
                                contentDescription = "Clock Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = if (people == 1) "" else people.toString(),
                        onValueChange = { input ->
                            // Only accept numeric input
                            people = input.toIntOrNull() ?: 0
                        },
                        label = { Text("People") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.group),
                                contentDescription = "Group Icon"
                            )
                        },
                        trailingIcon = {
                            Column {
                                // Up Arrow Button
                                IconButton(
                                    onClick = { people++ },
                                    modifier = Modifier.size(24.dp) // Make the button smaller
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Increase count"
                                    )
                                }
                                // Down Arrow Button
                                IconButton(
                                    // Disable the button if the count is 1
                                    enabled = people > 1,
                                    onClick = {
                                        // Add a check to prevent going below 1
                                        if (people > 1) {
                                            people--
                                        }
                                    },
                                    modifier = Modifier.size(24.dp) // Make the button smaller
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Decrease count"
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = if (price == 1000) "" else price.toString(),
                        onValueChange = { input ->
                            // Only accept numeric input
                            price = input.toIntOrNull() ?: 1000
                        },
                        label = { Text("Price per seat") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.attach_money),
                                contentDescription = "Money Icon"
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkylinePrimary,
                            unfocusedBorderColor = SkylineOnSurface.copy(alpha = 0.2f),
                            focusedLabelColor = SkylinePrimary,
                            unfocusedLabelColor = SkylineOnSurface.copy(alpha = 0.6f),
                            cursorColor = SkylinePrimary,
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* TODO: Handle login */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkylinePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Publish Ride", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.visibility),
                            contentDescription = "Date Icon"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Preview",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TripInfoCard()
                }
            }
        }
    }
}

@Composable
fun TripInfoCard() {
    ElevatedCard(
        modifier = Modifier.wrapContentWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Y",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StarRating(rating = 4.9f)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "From location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "From",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "To",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "To",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoColumn(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.car),
                                contentDescription = "Car",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        line1 = "Your",
                        line2 = "car"
                    )
                    InfoColumn(
                        line1 = "Date,",
                        line2 = "Time"
                    )
                    InfoColumn(
                        line1 = "$0.00",
                        line2 = "1 seats",
                        isPrice = true
                    )
                }
            }
        }
    }
}

@Composable
private fun StarRating(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "($rating)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoColumn(
    icon: (@Composable () -> Unit)? = null,
    line1: String,
    line2: String,
    isPrice: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                ProvideTextStyle(
                    value = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    icon()
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        Column(
            horizontalAlignment = if (isPrice) Alignment.End else Alignment.Start
        ) {
            Text(
                text = line1,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal,
                    textAlign = if (isPrice) TextAlign.End else TextAlign.Start
                )
            )
            Text(
                text = line2,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = if (isPrice) TextAlign.End else TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferRideScreenPreview() {
    RitherTheme(dynamicColor = false) {
        OfferRideScreen()
    }
}