package com.example.rither.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rither.ui.theme.RitherTheme

@Composable
fun ProfileScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProfileHeader()
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                InfoCard(label = "Phone", value = "+62 812 3456 7890")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                InfoCard(label = "Email", value = "kevin@example.com")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                InfoCard(label = "Rides Offered", value = "23")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                VerificationCard()
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                ActionButtons()
            }
        }
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            // Replace with your actual painter resource
            imageVector = Icons.Default.LocationOn,
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Kevin Hartono",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "4.8 average",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VerificationCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Verification",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Verified",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ActionButtons() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledTonalButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("Edit Profile", fontSize = 16.sp)
        }
        FilledTonalButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("View History", fontSize = 16.sp)
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    RitherTheme {
        ProfileScreen()
    }
}