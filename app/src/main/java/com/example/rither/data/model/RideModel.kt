package com.example.rither.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Ride(
    val id: String = "",
    val driverId: String? = null,
    val status: String = "pending",

    val pickUpLat: Double = 0.0,
    val pickUpLng: Double = 0.0,
    val pickUpAddress: String = "",

    val dropOffLat: Double = 0.0,
    val dropOffLng: Double = 0.0,
    val dropOffAddress: String = "",

    val rideType: String = "",
    val estimatedDuration: String = "",
    val distanceMeters: Int = 0,
    val price: Int = 0,

    val passengerId: List<String> = emptyList(),
    val currentPassengers: Int = 0,

    val createdAt: Long = System.currentTimeMillis(),
    val appointmentTime: Long? = null,
    val appointmentText: String? = null,
    val completedAt: Long? = null
)