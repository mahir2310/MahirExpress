package com.example.mahirexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Booking
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("bookings")
    
    private val _allBookings = MutableLiveData<List<Booking>>(emptyList())
    val allBookings: LiveData<List<Booking>> = _allBookings

    private val _totalRevenue = MutableLiveData<Double>(0.0)
    val totalRevenue: LiveData<Double> = _totalRevenue

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchAllBookings() {
        _isLoading.value = true
        _errorMessage.value = null

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingsList = mutableListOf<Booking>()
                var revenue = 0.0
                for (child in snapshot.children) {
                    val booking = child.getValue(Booking::class.java)
                    if (booking != null) {
                        bookingsList.add(booking)
                        revenue += booking.totalAmount
                    }
                }
                _allBookings.value = bookingsList
                _totalRevenue.value = revenue
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = error.message
            }
        })
    }
}
