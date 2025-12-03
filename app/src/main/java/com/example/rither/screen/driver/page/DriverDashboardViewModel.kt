package com.example.rither.screen.driver.page

import androidx.lifecycle.ViewModel
import com.example.rither.data.model.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class DriverDashboardViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _rideHistory = MutableStateFlow<List<Ride>>(emptyList())
    val rideHistory = _rideHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _requests = MutableStateFlow<List<Ride>>(emptyList())
    val requests: StateFlow<List<Ride>> = _requests

    private val _driverReservation = MutableStateFlow<Ride?>(null)
    val driverReservation: StateFlow<Ride?> = _driverReservation

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

    fun loadReservations() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("rides")
            .whereEqualTo("driverId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("DEBUG → error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    println("DEBUG → snapshot null")
                    return@addSnapshotListener
                }

                println("DEBUG → reservations snapshot size: ${snapshot.size()}")

                val list = snapshot.toObjects(Ride::class.java)
                println("DEBUG → parsed reservations size: ${list.size}")

                // Keep only the first assigned ride
                _driverReservation.value = list.firstOrNull()
            }
    }

    fun loadRequests() {
        db.collection("rides")
            .whereEqualTo("driverId", null)
            .addSnapshotListener { snapshot, error ->
                println("DEBUG → loadRequests triggered")

                if (error != null) {
                    println("❌ Firestore error: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    println("❌ Snapshot is null")
                    return@addSnapshotListener
                }

                println("DEBUG → snapshot size: ${snapshot.size()}")

                snapshot.documents.forEachIndexed { index, doc ->
                    println("----- DOC $index -----")
                    println("ID: ${doc.id}")
                    println("driverId: ${doc.get("driverId")}")
                    println("fields: ${doc.data}")
                }

                val list = snapshot.toObjects(Ride::class.java)
                println("DEBUG → parsed list size: ${list.size}")

                _requests.value = list
            }
    }

    fun acceptRideRequest(rideId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return

        val rideRef = db.collection("rides").document(rideId)

        val updates = mapOf(
            "driverId" to uid,
            "status" to "assigned",
        )

        rideRef.update(updates)
            .addOnSuccessListener {
                println("DEBUG → Ride $rideId successfully assigned to driver $uid")
                onSuccess()
            }
            .addOnFailureListener { e ->
                println("❌ ERROR → Failed to assign ride: ${e.message}")
                onFailure(e)
            }
    }

    fun cancelReservation(rideId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val rideRef = db.collection("rides").document(rideId)

        rideRef.get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onFailure(Exception("Ride does not exist"))
                    return@addOnSuccessListener
                }

                val passengerId = doc.getString("passengerId")
                val driverId = doc.getString("driverId")

                // CASE 1: No passenger AND no driver → DELETE
                if (passengerId.isNullOrEmpty() && driverId.isNullOrEmpty()) {
                    rideRef.delete()
                        .addOnSuccessListener {
                            println("DEBUG → Ride deleted (no passenger & no driver)")
                            onSuccess()
                        }
                        .addOnFailureListener(onFailure)
                    return@addOnSuccessListener
                }

                // CASE 2: Has passenger but NO driver → RESET to pending
                if (!passengerId.isNullOrEmpty() && driverId.isNullOrEmpty()) {
                    rideRef.update(
                        mapOf(
                            "status" to "pending",
                            "driverId" to null
                        )
                    ).addOnSuccessListener {
                        println("DEBUG → Ride reset to pending (has passenger, no driver)")
                        onSuccess()
                    }.addOnFailureListener(onFailure)
                    return@addOnSuccessListener
                }

                // CASE 3: Block if ride is active with driver + passenger
                onFailure(Exception("Cannot cancel — ride currently active or assigned"))
            }
            .addOnFailureListener(onFailure)
    }
}