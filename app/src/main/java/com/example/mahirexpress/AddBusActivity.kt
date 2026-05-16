package com.example.mahirexpress

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityAddBusBinding
import com.example.mahirexpress.models.Bus
import com.google.firebase.database.FirebaseDatabase

class AddBusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBusBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnAddBus.setOnClickListener {
            saveBus()
        }
    }

    private fun saveBus() {
        val busName = binding.etBusName.text.toString().trim()
        val regNumber = binding.etRegNumber.text.toString().trim()
        val totalSeatsStr = binding.etTotalSeats.text.toString().trim()
        val layout = binding.etLayout.text.toString().trim()

        if (busName.isEmpty() || regNumber.isEmpty() || totalSeatsStr.isEmpty() || layout.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val totalSeats = totalSeatsStr.toIntOrNull() ?: 0
        val busId = database.getReference("buses").push().key ?: return

        val bus = Bus(
            busId = busId,
            busName = busName,
            registrationNumber = regNumber,
            totalSeats = totalSeats,
            seatLayout = layout
        )

        binding.progressBar.visibility = View.VISIBLE
        database.getReference("buses").child(busId).setValue(bus)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bus added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add bus: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
