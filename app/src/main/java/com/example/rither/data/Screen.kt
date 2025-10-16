package com.example.rither.data

import androidx.annotation.StringRes
import com.example.rither.R

enum class Screen(@StringRes val title: Int) {
    Login(title = R.string.login),
    Signup(title = R.string.signup),
    Home(title = R.string.home),
    OfferRide(title = R.string.offerRide),
    OnBoarding(title = R.string.onBoarding),
    Profile(title = R.string.profile),
    RideDetails(title = R.string.rideDetails),
    Setting(title = R.string.settings),
    VerifyEmail(title = R.string.verifyEmail),
    Notifications(title = R.string.notifications)
}