package com.example.rither.screen.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

    var isDriver = mutableStateOf(false)
        private set

    init {
        loadUserDriverStatus()
    }

    suspend fun getUserName(): String? {
        val userId = auth.currentUser?.uid ?: return null

        println("DEBUG → FirebaseAuth UID = $userId")

        return try {
            val snap = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (!snap.exists()) {
                println("DEBUG → Firestore user doc not found for UID: $userId")
                return null
            }

            val name = snap.getString("name")
            println("DEBUG → Firestore name = $name")
            name
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
            null
        }
    }

    private fun loadUserDriverStatus() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                isDriver.value = user?.driver ?: false
            }
            .addOnFailureListener {
                isDriver.value = false
            }
    }

}