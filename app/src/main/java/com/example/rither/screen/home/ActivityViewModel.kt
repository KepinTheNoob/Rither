package com.example.rither.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rither.data.model.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _rideHistory = MutableStateFlow<List<Ride>>(emptyList())
    val rideHistory = _rideHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadRideHistory() {
        val userId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        db.collection("rides")
            .whereArrayContains("passengerId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                println("SUCCESS → Documents returned: ${result.size()}")
                val rides = result.documents.mapNotNull { doc ->
                    doc.toObject(Ride::class.java)?.copy(id = doc.id)
                }
                _rideHistory.value = rides
            }
            .addOnFailureListener { e ->
                println("🔥 FIRESTORE ERROR → ${e.message}")
                e.printStackTrace()
                _rideHistory.value = emptyList()
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }

    suspend fun getDriverName(driverId: String): String? {
        return try {
            val snap = db.collection("users")
                .document(driverId)
                .get()
                .await()

            if (!snap.exists()) {
                println("DEBUG → Firestore driver doc not found for UID: $driverId")
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
}