package com.example.mahirexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BookingViewModel : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

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
        _isLoading.value = true
        _errorMessage.value = null
        
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
                    _isSuccess.value = true
                } else {
                    _errorMessage.value = task.exception?.message
                }
                _isLoading.value = false
            }
    }
}
