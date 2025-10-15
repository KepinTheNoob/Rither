package com.example.rither

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rither.data.Screen
import com.example.rither.screen.home.HomeScreen
import com.example.rither.screen.login.LoginScreen
import com.example.rither.screen.offerRide.OfferRideScreen
import com.example.rither.screen.profile.ProfileScreen
import com.example.rither.screen.rideDetails.RideDetailScreen
import com.example.rither.screen.signup.SignupScreen

@Composable
fun MainContent(
    navController: NavHostController,
    modifier: Modifier = Modifier
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
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    context: Context,
    activity: Activity,
) {
    val navController = rememberNavController()
//    val auth = FirebaseAuth.getInstance()

    MainContent(
        navController = navController,
    )
}