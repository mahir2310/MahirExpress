package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityPassengerRegistrationBinding

class PassengerRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassengerRegistrationBinding
    private var routeId: String? = null
    private var totalAmount: Double = 0.0
    private var selectedSeats: ArrayList<String>? = null
    private var source: String? = null
    private var destination: String? = null
    private var busId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routeId = intent.getStringExtra("routeId")
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        selectedSeats = intent.getStringArrayListExtra("selectedSeats")
        source = intent.getStringExtra("source")
        destination = intent.getStringExtra("destination")
        busId = intent.getStringExtra("busId")

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnConfirm.setOnClickListener {
            val name = binding.etPassengerName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etPassengerName.error = "Name is required"
            } else {
                val intent = Intent(this, BookingSummaryActivity::class.java)
                intent.putExtra("routeId", routeId)
                intent.putExtra("source", source)
                intent.putExtra("destination", destination)
                intent.putExtra("busId", busId)
                intent.putStringArrayListExtra("selectedSeats", selectedSeats)
                intent.putExtra("totalAmount", totalAmount)
                
                val detailsBundle = Bundle()
                // Put the same name for all selected seats
                selectedSeats?.forEach { seat ->
                    detailsBundle.putString(seat, name)
                }
                intent.putExtra("passengerDetails", detailsBundle)

                startActivity(intent)
            }
        }
    }
}
