package com.example.rither.screen.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun logout() {
        auth.signOut()
    }
}
