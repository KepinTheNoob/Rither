package com.example.rither.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Ride(
    val id: String = "",
    val userId: String = "",
    val from: String = "",
    val to: String = "",
    val date: LocalDate,
    val time: LocalTime,
    val people: Int = 1,
    val price: Int = 1000
)