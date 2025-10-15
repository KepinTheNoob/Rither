package com.example.rither.screen.rideDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rither.R
import com.example.rither.ui.theme.RitherTheme

@Composable
fun RideDetailScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier.width(320.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    RideHeader()
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow(painter = painterResource(R.drawable.location_on), text = "Kemang") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "To",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Senayan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(painter = painterResource(R.drawable.clock), text = "Today, 5:30 PM")
                    Spacer(modifier = Modifier.height(16.dp))
                    PriceChip(price = "$4.50 / seat")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text("Book Seat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    ReviewsSection()
                }
            }
        }
    }
}

@Composable
fun RideHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F7FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Amira",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "(4.8)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StarRating(rating = 4.8f, starCount = 5)
            Text(
                text = "Toyota Yaris • B 1234 XY",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StarRating(rating: Float, starCount: Int) {
    Row {
        repeat(starCount) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun InfoRow(
    painter: Painter,
    text: String,
    content: @Composable RowScope.() -> Unit = {}
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
fun PriceChip(price: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = price,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReviewsSection() {
    Column {
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "\"Comfortable ride and friendly conversation!\"",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RideDetailCardPreview() {
    RitherTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            RideDetailScreen()
        }
    }
}