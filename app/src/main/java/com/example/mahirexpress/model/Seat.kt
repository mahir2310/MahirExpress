package com.example.mahirexpress.model

data class Seat(
    val seatNumber: String = "",
    val status: String = "Available", // Available, Selected, Booked
    val bookedBy: String = "",
    val bookingId: String = ""
)
