package com.example.rither.data.model

data class User(
    val id: String = "",
    val name: String = "",
    var rating: Double = 0.0,
    var studentId: String = "",
    var binusianId: String = "",
    var driver: Boolean = false,
    var phone: String = "",
    var email: String = ""
)
