package com.example.mahirexpress.model

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val routeId: String = "",
    val busId: String = "",
    val seatNumbers: List<String> = emptyList(),
    val passengerName: String = "",
    val passengerEmail: String = "",
    val passengerPhone: String = "",
    val journeyDate: String = "",
    val bookingDate: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val status: String = "Confirmed" // Confirmed, Cancelled
)
