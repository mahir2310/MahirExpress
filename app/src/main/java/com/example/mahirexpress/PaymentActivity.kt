package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityPaymentBinding
import com.example.mahirexpress.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val routeId = intent.getStringExtra("routeId") ?: ""
        val busId = intent.getStringExtra("busId") ?: "" // Added busId
        val source = intent.getStringExtra("source") ?: ""
        val destination = intent.getStringExtra("destination") ?: ""
        val busName = intent.getStringExtra("busName") ?: ""
        val selectedSeats = intent.getStringArrayListExtra("selectedSeats") ?: arrayListOf()
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        val detailsBundle = intent.getBundleExtra("passengerDetails")
        val journeyDate = intent.getStringExtra("journeyDate") ?: ""

        val passengerDetails = mutableMapOf<String, String>()
        detailsBundle?.let { bundle ->
            for (key in bundle.keySet()) {
                passengerDetails[key] = bundle.getString(key) ?: ""
            }
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvAmount.text = "Amount to Pay: ৳$totalAmount"

        binding.btnPayNow.setOnClickListener {
            val paymentDetail = binding.etPaymentDetail.text.toString().trim()
            if (paymentDetail.isEmpty()) {
                binding.etPaymentDetail.error = "Detail required"
                return@setOnClickListener
            }
            
            processBooking(routeId, busId, source, destination, busName, selectedSeats, passengerDetails, totalAmount, journeyDate)
        }
    }

    private fun processBooking(
        routeId: String,
        busId: String,
        source: String,
        destination: String,
        busName: String,
        seats: List<String>,
        details: Map<String, String>,
        amount: Double,
        journeyDate: String
    ) {
        val userId = auth.currentUser?.uid ?: return
        val bookingRef = database.getReference("bookings").push()
        val bookingId = bookingRef.key ?: return
        
        val booking = Booking(
            bookingId = bookingId,
            userId = userId,
            routeId = routeId,
            source = source,
            destination = destination,
            busName = busName,
            seats = seats,
            passengerDetails = details,
            totalAmount = amount,
            status = "confirmed",
            journeyDate = journeyDate
        )

        binding.progressBar.visibility = View.VISIBLE
        binding.btnPayNow.isEnabled = false

        bookingRef.setValue(booking)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update seats using busId as the identifier
                    updateSeatStatus(busId, seats, bookingId)
                } else {
                    if (!isFinishing) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnPayNow.isEnabled = true
                        Toast.makeText(this, "Booking failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun updateSeatStatus(busId: String, seats: List<String>, bookingId: String) {
        val seatUpdates = mutableMapOf<String, Any>()
        seats.forEach { seat ->
            seatUpdates["$seat/status"] = "booked"
            seatUpdates["$seat/bookingId"] = bookingId
            seatUpdates["$seat/bookedBy"] = auth.currentUser?.uid ?: ""
        }

        // Must update under "seats" -> "busId"
        database.getReference("seats").child(busId).updateChildren(seatUpdates)
            .addOnCompleteListener { task ->
                if (!isFinishing) {
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Payment Successful & Ticket Booked!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, BookingConfirmationActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update seats", Toast.LENGTH_SHORT).show()
                        binding.btnPayNow.isEnabled = true
                    }
                }
            }
    }
}
