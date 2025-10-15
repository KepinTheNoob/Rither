package com.example.rither.screen.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rither.R
import com.example.rither.ui.theme.RitherTheme

data class OnboardingPage(
    val icon: Painter,
    val title: String,
    val description: String
)

@Composable
fun getOnboardingPage(): List<OnboardingPage> {
    return listOf(
        OnboardingPage(
            icon = painterResource(R.drawable.car),
            title = "Carpool with ease",
            description = "Offer or request rides with other students"
        ),
        OnboardingPage(
            icon = painterResource(R.drawable.savings),
            title = "Save on Costs",
            description = "Split your fuel and and tolls while traveling together."
        ),
        OnboardingPage(
            icon = painterResource(R.drawable.nest_eco_leaf),
            title = "Eco-friendly trips",
            description = "Reduce emissions by sharing your ride and making travel greener."
        )
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnBoardingCard() {
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = getOnboardingPage().size

    ElevatedCard(
        modifier = Modifier
            .width(300.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth } with
                            slideOutHorizontally { fullWidth -> -fullWidth }
                }, label = "Onboarding Animation"
            ) { pageIndex ->
                val pageData = getOnboardingPage()[pageIndex]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = pageData.icon,
                            contentDescription = pageData.title,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = pageData.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pageData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(40.dp) // Set a fixed height to prevent layout jumps
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PageIndicator(pageCount = totalPages, currentPage = currentPage)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO: Handle Skip */ }) {
                    Text("Skip")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        currentPage = (currentPage + 1) % totalPages
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(if (currentPage == totalPages - 1) "Finish" else "Next", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OnBoardingCardPreview() {
    RitherTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            OnBoardingCard()
        }
    }
}