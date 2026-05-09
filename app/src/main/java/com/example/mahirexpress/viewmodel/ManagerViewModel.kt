package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Bus
import com.example.mahirexpress.model.Route
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManagerViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    
    val routes = mutableStateListOf<Route>()
    val buses = mutableStateListOf<Bus>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchData() {
        isLoading = true
        errorMessage = null

        // Fetch Routes
        db.getReference("routes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                routes.clear()
                for (child in snapshot.children) {
                    child.getValue(Route::class.java)?.let { routes.add(it) }
                }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                errorMessage = error.message
            }
        })

        // Fetch Buses
        db.getReference("buses").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                buses.clear()
                for (child in snapshot.children) {
                    child.getValue(Bus::class.java)?.let { buses.add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
