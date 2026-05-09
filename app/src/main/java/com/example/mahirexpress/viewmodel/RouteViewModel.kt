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
    val allSources = mutableStateListOf<String>()
    val allDestinations = mutableStateListOf<String>()
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchLocations()
    }

    private fun fetchLocations() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sources = mutableSetOf<String>()
                val destinations = mutableSetOf<String>()
                for (child in snapshot.children) {
                    val route = child.getValue(Route::class.java)
                    route?.let {
                        sources.add(it.source)
                        destinations.add(it.destination)
                    }
                }
                allSources.clear()
                allSources.addAll(sources.sorted())
                allDestinations.clear()
                allDestinations.addAll(destinations.sorted())
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun searchRoutes(source: String, destination: String, date: String) {
        isLoading = true
        errorMessage = null
        routes.clear()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                routes.clear()
                for (child in snapshot.children) {
                    val route = child.getValue(Route::class.java)
                    if (route != null && 
                        route.source.equals(source, ignoreCase = true) && 
                        route.destination.equals(destination, ignoreCase = true) &&
                        route.date == date) {
                        routes.add(route)
                    }
                }
                isLoading = false
                if (routes.isEmpty()) {
                    errorMessage = "No buses found for this route on $date."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                errorMessage = error.message
            }
        })
    }
}
