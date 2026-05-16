package com.example.mahirexpress.models

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer", // customer, manager, admin
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
