package com.example.rither

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rither.data.Screen
import com.example.rither.screen.home.HomeScreen
import com.example.rither.screen.login.LoginScreen
import com.example.rither.screen.notifications.NotificationsScreen
import com.example.rither.screen.setting.SettingsScreen
import com.example.rither.screen.notifications.sampleNotifications
import com.example.rither.screen.offerRide.OfferRideScreen
import com.example.rither.screen.profile.ProfileScreen
import com.example.rither.screen.rideDetails.RideDetailScreen
import com.example.rither.screen.signup.SignupScreen
import com.example.rither.screen.verifyEmail.VerifyEmailScreen
import com.example.rither.ui.theme.RitherTheme

@Composable
fun MainContent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.name,
        modifier = modifier
    ) {
        composable(Screen.Login.name) {
            LoginScreen(
                navController = navController,
            )
        }
        composable(Screen.Signup.name) {
            SignupScreen(
                navController = navController,
            )
        }
        composable(Screen.Home.name) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Screen.OfferRide.name) {
            OfferRideScreen(
//                navController = navController
            )
        }
        composable(Screen.Profile.name) {
            ProfileScreen(
//                navController = navController
            )
        }
        composable(Screen.RideDetails.name) {
            RideDetailScreen(
//                navController = navController
            )
        }
        composable(Screen.VerifyEmail.name) {
            VerifyEmailScreen(
                navController = navController
            )
        }
        composable(Screen.Notifications.name) {
            NotificationsScreen(
                notifications = sampleNotifications,
                navController = navController
            )
        }
        composable(Screen.Setting.name) {
            SettingsScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    context: Context,
    activity: Activity,
    intent: MainActivity,
) {
    val navController = rememberNavController()
    var isDarkTheme by remember { mutableStateOf(false) }
    val intent = intent

    RitherTheme(darkTheme = isDarkTheme) {
        MainContent(
            navController = navController,
            // 3. PASS THE STATE AND THE FUNCTION TO CHANGE IT DOWN.
            isDarkTheme = isDarkTheme,
            onThemeChange = { isDarkTheme = it }
        )
    }
}