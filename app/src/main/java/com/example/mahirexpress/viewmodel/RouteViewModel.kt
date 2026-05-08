package com.example.mahirexpress.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Route
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RouteViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("routes")
    
    val routes = mutableStateListOf<Route>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun searchRoutes(source: String, destination: String) {
        isLoading = true
        errorMessage = null
        routes.clear()

        // In a real app, we'd use a query, but for our lab project, 
        // we fetch all and filter locally for simplicity and reliability.
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                routes.clear()
                for (child in snapshot.children) {
                    val route = child.getValue(Route::class.java)
                    if (route != null && 
                        route.source.equals(source, ignoreCase = true) && 
                        route.destination.equals(destination, ignoreCase = true)) {
                        routes.add(route)
                    }
                }
                isLoading = false
                if (routes.isEmpty()) {
                    errorMessage = "No buses found for this route."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                errorMessage = error.message
            }
        })
    }
}
