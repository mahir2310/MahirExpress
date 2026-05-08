package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Booking
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("bookings")
    
    val allBookings = mutableStateListOf<Booking>()
    var totalRevenue by mutableStateOf(0.0)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchAllBookings() {
        isLoading = true
        errorMessage = null

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allBookings.clear()
                var revenue = 0.0
                for (child in snapshot.children) {
                    val booking = child.getValue(Booking::class.java)
                    if (booking != null) {
                        allBookings.add(booking)
                        revenue += booking.totalAmount
                    }
                }
                totalRevenue = revenue
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                errorMessage = error.message
            }
        })
    }
}
