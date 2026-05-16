package com.example.mahirexpress

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityAddRouteBinding
import com.example.mahirexpress.models.Bus
import com.example.mahirexpress.models.Route
import com.google.firebase.database.*
import java.util.*

class AddRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRouteBinding
    private lateinit var database: FirebaseDatabase
    private val busList = mutableListOf<Bus>()
    private val busNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadBuses()

        binding.etDeparture.setOnClickListener { showTimePicker { time -> binding.etDeparture.setText(time) } }
        binding.etArrival.setOnClickListener { showTimePicker { time -> binding.etArrival.setText(time) } }

        binding.btnAddRoute.setOnClickListener {
            saveRoute()
        }
    }

    private fun loadBuses() {
        database.getReference("buses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                busList.clear()
                busNames.clear()
                for (busSnapshot in snapshot.children) {
                    val bus = busSnapshot.getValue(Bus::class.java)
                    if (bus != null) {
                        busList.add(bus)
                        busNames.add(bus.busName)
                    }
                }
                val adapter = ArrayAdapter(this@AddRouteActivity, android.R.layout.simple_spinner_item, busNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerBus.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val amPm = if (selectedHour < 12) "AM" else "PM"
            val hourDisplay = if (selectedHour % 12 == 0) 12 else selectedHour % 12
            val time = String.format(Locale.getDefault(), "%02d:%02d %s", hourDisplay, selectedMinute, amPm)
            onTimeSelected(time)
        }, hour, minute, false).show()
    }

    private fun saveRoute() {
        val source = binding.etSource.text.toString().trim()
        val destination = binding.etDestination.text.toString().trim()
        val departure = binding.etDeparture.text.toString().trim()
        val arrival = binding.etArrival.text.toString().trim()
        val fareStr = binding.etFare.text.toString().trim()

        if (source.isEmpty() || destination.isEmpty() || departure.isEmpty() || 
            arrival.isEmpty() || fareStr.isEmpty() || binding.spinnerBus.selectedItem == null) {
            Toast.makeText(this, "Please fill all fields and select a bus", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedBus = busList[binding.spinnerBus.selectedItemPosition]
        val fare = fareStr.toDoubleOrNull() ?: 0.0
        val routeRef = database.getReference("routes").push()
        val routeId = routeRef.key ?: return
        
        val route = Route(
            routeId = routeId,
            source = source,
            destination = destination,
            departureTime = departure,
            arrivalTime = arrival,
            fare = fare,
            availableSeats = selectedBus.totalSeats,
            busId = selectedBus.busId,
            busName = selectedBus.busName
        )

        binding.progressBar.visibility = View.VISIBLE
        routeRef.setValue(route)
            .addOnCompleteListener { task ->
                if (!isFinishing) {
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Route added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to add route: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
