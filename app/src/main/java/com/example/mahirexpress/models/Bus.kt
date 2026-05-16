package com.example.mahirexpress.models

data class Bus(
    val busId: String = "",
    val busName: String = "",
    val registrationNumber: String = "",
    val totalSeats: Int = 0,
    val manager: String = "",
    val seatLayout: String = "" // e.g., "2x2"
)
