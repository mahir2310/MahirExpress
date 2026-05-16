package com.example.mahirexpress.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mahirexpress.model.Route
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RouteViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference("routes")
    
    private val _routes = MutableLiveData<List<Route>>(emptyList())
    val routes: LiveData<List<Route>> = _routes

    private val _allSources = MutableLiveData<List<String>>(emptyList())
    val allSources: LiveData<List<String>> = _allSources

    private val _allDestinations = MutableLiveData<List<String>>(emptyList())
    val allDestinations: LiveData<List<String>> = _allDestinations
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

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
                _allSources.value = sources.sorted()
                _allDestinations.value = destinations.sorted()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun searchRoutes(source: String, destination: String, date: String) {
        _isLoading.value = true
        _errorMessage.value = null

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val routesList = mutableListOf<Route>()
                for (child in snapshot.children) {
                    val route = child.getValue(Route::class.java)
                    if (route != null && 
                        route.source.equals(source, ignoreCase = true) && 
                        route.destination.equals(destination, ignoreCase = true) &&
                        route.date == date) {
                        routesList.add(route)
                    }
                }
                _routes.value = routesList
                _isLoading.value = false
                if (routesList.isEmpty()) {
                    _errorMessage.value = "No buses found for this route on $date."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = error.message
            }
        })
    }
}
