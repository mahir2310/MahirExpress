package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityBookingSummaryBinding
import java.text.SimpleDateFormat
import java.util.*

class BookingSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val routeId = intent.getStringExtra("routeId") ?: ""
        val source = intent.getStringExtra("source") ?: ""
        val destination = intent.getStringExtra("destination") ?: ""
        val busId = intent.getStringExtra("busId") ?: ""
        val selectedSeats = intent.getStringArrayListExtra("selectedSeats") ?: arrayListOf()
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        val detailsBundle = intent.getBundleExtra("passengerDetails")

        val passengerDetails = mutableMapOf<String, String>()
        detailsBundle?.let { bundle ->
            for (key in bundle.keySet()) {
                passengerDetails[key] = bundle.getString(key) ?: ""
            }
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvRouteSummary.text = "$source to $destination"
        binding.tvSeatsSummary.text = selectedSeats.joinToString(", ")
        binding.tvTotalFareSummary.text = "৳$totalAmount"
        
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        binding.tvDateSummary.text = dateStr

        val detailsText = StringBuilder()
        passengerDetails.forEach { (seat, detail) ->
            detailsText.append("Seat $seat: $detail\n")
        }
        binding.tvPassengerDetails.text = detailsText.toString()

        binding.btnConfirmBooking.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("routeId", routeId)
            intent.putExtra("source", source)
            intent.putExtra("destination", destination)
            intent.putExtra("busName", busId)
            intent.putStringArrayListExtra("selectedSeats", selectedSeats)
            intent.putExtra("totalAmount", totalAmount)
            intent.putExtra("passengerDetails", detailsBundle)
            intent.putExtra("journeyDate", dateStr)
            startActivity(intent)
        }
    }
}
