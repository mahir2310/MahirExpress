package com.example.mahirexpress.models

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val routeId: String = "",
    val source: String = "",
    val destination: String = "",
    val busName: String = "",
    val seats: List<String> = emptyList(),
    val passengerDetails: Map<String, String> = emptyMap(),
    val bookingDate: Long = System.currentTimeMillis(),
    val journeyDate: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "confirmed"
)
