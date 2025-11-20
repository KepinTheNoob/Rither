package com.example.rither.screen.rideSelection

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.rither.BuildConfig.MAPS_API_KEY
import com.example.rither.data.Screen
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

class RideSelectionViewModel : ViewModel() {

    // ✔ Correct state holder for Compose
    var lastRouteDurationText = mutableStateOf("...")

    var lastRouteDurationValue: Int = 0

    // --- Retrofit interface returning RAW JSON ---
    interface DirectionsRawApi {
        @GET("maps/api/directions/json")
        suspend fun getRawRoute(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("key") apiKey: String
        ): String
    }

    val directionsRawService: DirectionsRawApi = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(DirectionsRawApi::class.java)

    // --- Main function ---
    fun getRoutePoints(start: LatLng, end: LatLng, onPolylineReady: (List<LatLng>) -> Unit) {
        viewModelScope.launch {
            try {
                val origin = "${start.latitude},${start.longitude}"
                val dest = "${end.latitude},${end.longitude}"

                val raw = withContext(Dispatchers.IO) {
                    directionsRawService.getRawRoute(origin, dest, MAPS_API_KEY)
                }

                val json = JSONObject(raw)
                val routes = json.getJSONArray("routes")
                if (routes.length() == 0) {
                    onPolylineReady(emptyList())
                    return@launch
                }

                val route = routes.getJSONObject(0)
                val leg = route.getJSONArray("legs").getJSONObject(0)

                val durationText = leg.getJSONObject("duration").getString("text")
                val durationValue = leg.getJSONObject("duration").getInt("value")

                // ✔ Update state on Main thread
                lastRouteDurationText.value = durationText
                lastRouteDurationValue = durationValue

                val encoded = route
                    .getJSONObject("overview_polyline")
                    .getString("points")

                val polyline = decodePolyline(encoded)

                onPolylineReady(polyline)

            } catch (e: Exception) {
                e.printStackTrace()
                onPolylineReady(emptyList())
            }
        }
    }


    // Response (only what we need)
    data class DirectionsResponse(
        val routes: List<Route>
    )

    data class Route(
        val overview_polyline: Polyline
    )

    data class Polyline(
        val points: String
    )

    // Polyline decoder
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1F) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1F) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }

    fun requestRide(
        pickupName: String,
        dropoffName: String,
        pickupLatLng: LatLng,
        dropoffLatLng: LatLng,
        selectedRide: String,
        distance: Int,
        durationText: String,
        price: Int,
        navController: NavController,
        isReserved: Boolean,
        appointmentTime: Long?,
        appointmentText: String?
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val docRef = db.collection("rides").document()
        val rideId = docRef.id

        val rideData = hashMapOf(
            "id" to rideId,
            "driverId" to null,
            "status" to "pending",

            "pickUpLat" to pickupLatLng.latitude,
            "pickUpLng" to pickupLatLng.longitude,
            "pickUpAddress" to pickupName,

            "dropOffLat" to dropoffLatLng.latitude,
            "dropOffLng" to dropoffLatLng.longitude,
            "dropOffAddress" to dropoffName,

            "rideType" to selectedRide,
            "distanceMeters" to distance,
            "estimatedDuration" to durationText,
            "price" to price,

            "passengerId" to listOf(userId),
            "currentPassengers" to 1,

            "createdAt" to System.currentTimeMillis(),
            "completedAt" to null,
            "isReserved" to isReserved,
            "appointmentTime" to appointmentTime,
            "appointmentText" to appointmentText
        )

        docRef.set(rideData)
            .addOnSuccessListener {
                println("Ride created with ID: $rideId")
                navController.navigate(Screen.Activity.name)
            }
            .addOnFailureListener { e ->
                println("Error creating ride: ${e.message}")
            }
    }

}

