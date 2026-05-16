package com.example.mahirexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Bus
import com.example.mahirexpress.model.Route
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManagerViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    
    private val _routes = MutableLiveData<List<Route>>(emptyList())
    val routes: LiveData<List<Route>> = _routes

    private val _buses = MutableLiveData<List<Bus>>(emptyList())
    val buses: LiveData<List<Bus>> = _buses

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchData() {
        _isLoading.value = true
        _errorMessage.value = null

        // Fetch Routes
        db.getReference("routes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val routesList = mutableListOf<Route>()
                for (child in snapshot.children) {
                    child.getValue(Route::class.java)?.let { routesList.add(it) }
                }
                _routes.value = routesList
                _isLoading.value = false
            }
            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = error.message
            }
        })

        // Fetch Buses
        db.getReference("buses").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val busesList = mutableListOf<Bus>()
                for (child in snapshot.children) {
                    child.getValue(Bus::class.java)?.let { busesList.add(it) }
                }
                _buses.value = busesList
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
