package com.example.mahirexpress.model

data class Bus(
    val busId: String = "",
    val busName: String = "",
    val registrationNumber: String = "",
    val totalSeats: Int = 40,
    val managerId: String = "",
    val type: String = "AC" // AC, Non-AC
)
