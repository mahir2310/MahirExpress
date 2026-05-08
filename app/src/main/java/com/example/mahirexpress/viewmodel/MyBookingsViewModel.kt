package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyBookingsViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("bookings")
    private val auth = FirebaseAuth.getInstance()
    
    val bookings = mutableStateListOf<Booking>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchUserBookings() {
        val userId = auth.currentUser?.uid ?: return
        isLoading = true
        errorMessage = null

        database.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookings.clear()
                    for (child in snapshot.children) {
                        val booking = child.getValue(Booking::class.java)
                        if (booking != null) {
                            bookings.add(booking)
                        }
                    }
                    isLoading = false
                    if (bookings.isEmpty()) {
                        errorMessage = "No bookings found."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                    errorMessage = error.message
                }
            })
    }
}
