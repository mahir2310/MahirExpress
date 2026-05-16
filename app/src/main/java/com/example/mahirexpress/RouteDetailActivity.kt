package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityRouteDetailBinding

class RouteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val routeId = intent.getStringExtra("routeId")
        val source = intent.getStringExtra("source")
        val destination = intent.getStringExtra("destination")
        val fare = intent.getDoubleExtra("fare", 0.0)
        val departure = intent.getStringExtra("departure")
        val arrival = intent.getStringExtra("arrival")
        val busName = intent.getStringExtra("busName")
        val journeyDate = intent.getStringExtra("journeyDate")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvDetailRoute.text = "$source to $destination"
        binding.tvDetailBus.text = "Bus: $busName"
        binding.tvDetailDeparture.text = departure
        binding.tvDetailArrival.text = arrival
        binding.tvDetailFare.text = "Fare: ৳$fare"

        binding.btnSelectSeats.setOnClickListener {
            val intent = Intent(this, SeatSelectionActivity::class.java).apply {
                putExtra("routeId", routeId)
                putExtra("fare", fare)
                putExtra("source", source)
                putExtra("destination", destination)
                putExtra("busId", busName) // Passing bus name as ID for simplicity in this flow
                putExtra("journeyDate", journeyDate)
            }
            startActivity(intent)
        }
    }
}
