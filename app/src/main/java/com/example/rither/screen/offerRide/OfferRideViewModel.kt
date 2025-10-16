package com.example.rither.screen.offerRide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rither.data.model.Ride
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

sealed class RideState {
    object Idle : RideState()
    object Loading : RideState()
    data class Success(val rides: List<Ride> = emptyList()) : RideState()
    data class Error(val message: String) : RideState()
    object RideCreated : RideState()
    object RideUpdated : RideState()
    object RideDeleted : RideState()
}

class OfferRideViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _rideState = MutableLiveData<RideState>(RideState.Idle)
    val rideState: LiveData<RideState> = _rideState

    // 🔹 CREATE
    fun createRide(from: String, to: String, date: LocalDate?, time: LocalTime?, people: Int, price: Int) {
        val user = auth.currentUser ?: run {
            _rideState.value = RideState.Error("User not authenticated")
            return
        }

        if (from.isEmpty() || to.isEmpty() || date == null || time == null) {
            _rideState.value = RideState.Error("All fields must be filled")
            return
        }

        if (people <= 0) {
            _rideState.value = RideState.Error("Number of people must be greater than 0")
            return
        }

        if (price <= 0) {
            _rideState.value = RideState.Error("Price must be greater than 0")
            return
        }

        val newRide = Ride(
            id = db.collection("rides").document().id,
            userId = user.uid,
            from = from,
            to = to,
            date = date,
            time = time,
            people = people,
            price = price
        )

        _rideState.value = RideState.Loading
        db.collection("rides").document(newRide.id)
            .set(newRide)
            .addOnSuccessListener {
                _rideState.value = RideState.RideCreated
            }
            .addOnFailureListener { e ->
                _rideState.value = RideState.Error(e.message ?: "Failed to create ride")
            }
    }

    // 🔹 READ (All rides by user)
    fun fetchRides() {
        val user = auth.currentUser ?: run {
            _rideState.value = RideState.Error("User not authenticated")
            return
        }

        _rideState.value = RideState.Loading
        db.collection("rides")
            .whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val rides = snapshot.documents.mapNotNull { it.toObject(Ride::class.java) }
                _rideState.value = RideState.Success(rides)
            }
            .addOnFailureListener { e ->
                _rideState.value = RideState.Error(e.message ?: "Failed to fetch rides")
            }
    }

    // 🔹 UPDATE
    fun updateRide(rideId: String, updatedData: Map<String, Any>) {
        _rideState.value = RideState.Loading
        db.collection("rides").document(rideId)
            .update(updatedData)
            .addOnSuccessListener {
                _rideState.value = RideState.RideUpdated
            }
            .addOnFailureListener { e ->
                _rideState.value = RideState.Error(e.message ?: "Failed to update ride")
            }
    }

    // 🔹 DELETE
    fun deleteRide(rideId: String) {
        _rideState.value = RideState.Loading
        db.collection("rides").document(rideId)
            .delete()
            .addOnSuccessListener {
                _rideState.value = RideState.RideDeleted
            }
            .addOnFailureListener { e ->
                _rideState.value = RideState.Error(e.message ?: "Failed to delete ride")
            }
    }
}
