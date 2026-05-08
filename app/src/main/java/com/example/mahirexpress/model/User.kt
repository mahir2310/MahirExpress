package com.example.mahirexpress.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "Customer", // Admin, Manager, Customer
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
