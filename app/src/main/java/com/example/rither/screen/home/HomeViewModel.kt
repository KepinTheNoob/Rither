package com.example.rither.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rither.data.model.Ride
import com.example.rither.data.model.RideWithUser
import com.example.rither.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var rideList: List<RideWithUser> = emptyList()
        private set

    var isLoading: Boolean = true
        private set

    suspend fun getUserName(): String? {
        val userId = auth.currentUser?.uid ?: return null

        return try {
            val name = db.collection("users")
                .document(userId)
                .get()
                .await()

            name.getString("name")
        } catch (e: Exception) {
            println("❌ Error fetching user name: ${e.message}")
            null
        }
    }
}