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

    fun getUpcomingReservations(): List<Ride> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        val result = _rideHistory.value.filter { ride ->
            ride.status == "pending" &&
                    ride.passengerId.contains(userId)
        }

        return result
    }

    fun getAssignedReservations(): List<Ride> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        println("DEBUG → Filtering assigned reservations for userId: $userId")

        return _rideHistory.value.filter { ride ->
            ride.passengerId.contains(userId) &&
                    ride.status.equals("assigned", ignoreCase = true)
        }
    }


    fun cancelReservation(
        rideId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return
        val rideRef = db.collection("rides").document(rideId)

        rideRef.get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onFailure(Exception("Ride does not exist"))
                    return@addOnSuccessListener
                }

                val passengerList = doc.get("passengerId") as? List<String> ?: emptyList()
                val driverId = doc.getString("driverId")

                val newPassengerList = passengerList.filter { it != uid }

                // --- CASE 1: After removal → No passenger + No driver → DELETE
                if (newPassengerList.isEmpty() && driverId.isNullOrEmpty()) {
                    rideRef.delete()
                        .addOnSuccessListener {
                            println("DEBUG → Ride deleted (no passenger & no driver)")
                            onSuccess()
                        }
                        .addOnFailureListener(onFailure)
                    return@addOnSuccessListener
                }

                // --- CASE 2: Has passengers, NO driver → RESET to pending
                if (newPassengerList.isNotEmpty() && driverId.isNullOrEmpty()) {
                    rideRef.update(
                        mapOf(
                            "passengerId" to newPassengerList,
                            "status" to "pending"
                        )
                    ).addOnSuccessListener {
                        println("DEBUG → Passenger removed, ride reset to pending")
                        onSuccess()
                    }.addOnFailureListener(onFailure)
                    return@addOnSuccessListener
                }

                // --- CASE 3: Driver exists → passenger leaves but ride continues
                if (!driverId.isNullOrEmpty()) {
                    rideRef.update(
                        mapOf(
                            "passengerId" to newPassengerList
                        )
                    ).addOnSuccessListener {
                        println("DEBUG → Passenger removed, ride still assigned to driver")
                        onSuccess()
                    }.addOnFailureListener(onFailure)
                    return@addOnSuccessListener
                }
            }
            .addOnFailureListener(onFailure)
    }

    fun loadActiveReservations() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("rides")
            .whereArrayContains("passengerId", userId)
            .get()
            .addOnSuccessListener { result ->
                val active = result.documents.mapNotNull { doc ->
                    doc.toObject(Ride::class.java)?.copy(id = doc.id)
                }.filter { it.status != "complete" } // pending + assigned

                println("DEBUG → Active reservations loaded: ${active.size}")

                // append to existing rideHistory
                _rideHistory.value = _rideHistory.value + active
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}