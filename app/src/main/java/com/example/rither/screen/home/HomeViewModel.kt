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
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _latestRide = MutableStateFlow<Ride?>(null)
    val latestRide = _latestRide.asStateFlow()

    init {
        loadUserDriverStatus()
    }

    suspend fun getUserName(): String? {
        val userId = auth.currentUser?.uid ?: return null

        return try {
            val snap = db.collection("users")
                .document(userId)
                .get()
                .await()

            if (!snap.exists()) {
                return null
            }

            val name = snap.getString("name")
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

    fun loadLatestRide() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("rides")
            .whereArrayContains("passengerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                val ride = snap.documents.firstOrNull()?.toObject(Ride::class.java)
                _latestRide.value = ride
            }
            .addOnFailureListener { e ->
                println("❌ Latest Ride Load Error → ${e.message}")
            }
    }

    suspend fun getDriverName(driverId: String): String? {
        return try {
            val snap = db.collection("users")
                .document(driverId)
                .get()
                .await()

            if (!snap.exists()) {
                return null
            }

            val name = snap.getString("name")
            name
        } catch (e: Exception) {
            null
        }
    }
}