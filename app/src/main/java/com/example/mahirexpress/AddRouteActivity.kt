package com.example.mahirexpress

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityAddRouteBinding
import com.example.mahirexpress.models.Route
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddRouteBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.etDeparture.setOnClickListener { showTimePicker { time -> binding.etDeparture.setText(time) } }
        binding.etArrival.setOnClickListener { showTimePicker { time -> binding.etArrival.setText(time) } }

        binding.btnAddRoute.setOnClickListener {
            saveRoute()
        }
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
        val busId = binding.etBusId.text.toString().trim()

        if (source.isEmpty() || destination.isEmpty() || departure.isEmpty() || 
            arrival.isEmpty() || fareStr.isEmpty() || busId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

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
            availableSeats = 40,
            busId = busId,
            busName = busId // Simple mapping for now
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
