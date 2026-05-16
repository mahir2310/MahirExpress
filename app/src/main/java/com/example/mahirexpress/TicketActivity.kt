package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityTicketBinding
import com.example.mahirexpress.utils.PrefManager

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val route = intent.getStringExtra("route") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val seats = intent.getStringExtra("seats") ?: ""
        val bus = intent.getStringExtra("bus") ?: ""
        val amount = intent.getStringExtra("amount") ?: ""
        val bookingId = intent.getStringExtra("bookingId") ?: ""

        binding.tvTicketRoute.text = route
        binding.tvTicketDate.text = "Date: $date"
        binding.tvTicketSeats.text = "Seats: $seats"
        binding.tvTicketBus.text = "Bus: $bus"
        binding.tvTicketAmount.text = "Total Amount: $amount"
        binding.tvTicketId.text = "Booking ID: $bookingId"

        binding.btnDownload.setOnClickListener {
            Toast.makeText(this, "Downloading ticket...", Toast.LENGTH_SHORT).show()
        }
    }
}
