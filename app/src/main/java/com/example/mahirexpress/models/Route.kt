package com.example.mahirexpress.models

data class Route(
    val routeId: String = "",
    val source: String = "",
    val destination: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val distance: String = "",
    val fare: Double = 0.0,
    val availableSeats: Int = 0,
    val busId: String = "",
    val busName: String = "",
    val rating: Float = 4.5f
)
