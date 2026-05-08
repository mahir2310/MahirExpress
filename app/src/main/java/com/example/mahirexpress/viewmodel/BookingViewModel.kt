package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BookingViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun confirmBooking(
        routeId: String,
        busId: String,
        seats: List<String>,
        passengerName: String,
        passengerEmail: String,
        passengerPhone: String,
        totalAmount: Double,
        journeyDate: String
    ) {
        isLoading = true
        errorMessage = null
        
        val bookingRef = database.getReference("bookings")
        val bookingId = bookingRef.push().key ?: return
        
        val booking = Booking(
            bookingId = bookingId,
            userId = auth.currentUser?.uid ?: "",
            routeId = routeId,
            busId = busId,
            seatNumbers = seats,
            passengerName = passengerName,
            passengerEmail = passengerEmail,
            passengerPhone = passengerPhone,
            journeyDate = journeyDate,
            totalAmount = totalAmount,
            status = "Confirmed"
        )

        bookingRef.child(bookingId).setValue(booking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // For a real app, we'd also decrement availableSeats in the 'routes' node here.
                    isSuccess = true
                } else {
                    errorMessage = task.exception?.message
                }
                isLoading = false
            }
    }
}
