package com.example.rither.screen.notifications

// Enum to represent the different types of notifications
enum class NotificationType {
    SUCCESS,
    CANCELLED
}

// Data class to hold the information for a single notification item
data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val timestamp: String,
    val type: NotificationType
)