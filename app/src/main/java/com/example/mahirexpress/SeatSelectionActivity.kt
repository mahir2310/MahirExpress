package com.example.mahirexpress

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import com.example.mahirexpress.databinding.ActivitySeatSelectionBinding
import com.google.firebase.database.*

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatSelectionBinding
    private lateinit var database: DatabaseReference
    private var routeId: String? = null
    private var fare: Double = 0.0
    private var source: String? = null
    private var destination: String? = null
    private var busId: String? = null
    private val selectedSeats = mutableListOf<String>()
    private val bookedSeats = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routeId = intent.getStringExtra("routeId")
        fare = intent.getDoubleExtra("fare", 0.0)
        source = intent.getStringExtra("source")
        destination = intent.getStringExtra("destination")
        busId = intent.getStringExtra("busId")

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // FIX: The seat data must be uniquely keyed by the routeId, NOT busId.
        // If it's keyed by busId, all routes using that bus will share the same bookings.
        database = FirebaseDatabase.getInstance().getReference("seats").child(routeId ?: "default")

        fetchBookedSeats()

        binding.btnContinue.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
            } else if (selectedSeats.size > 5) {
                Toast.makeText(this, "You can book maximum 5 seats", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, PassengerRegistrationActivity::class.java)
                intent.putExtra("routeId", routeId)
                intent.putExtra("fare", fare)
                intent.putExtra("source", source)
                intent.putExtra("destination", destination)
                intent.putExtra("busId", busId)
                intent.putStringArrayListExtra("selectedSeats", ArrayList(selectedSeats))
                intent.putExtra("totalAmount", selectedSeats.size * fare)
                startActivity(intent)
            }
        }
    }

    private fun fetchBookedSeats() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookedSeats.clear()
                for (seatSnapshot in snapshot.children) {
                    val status = seatSnapshot.child("status").getValue(String::class.java)
                    if (status == "booked") {
                        bookedSeats.add(seatSnapshot.key ?: "")
                    }
                }
                setupSeatGrid()
            }

            override fun onCancelled(error: DatabaseError) {
                setupSeatGrid()
            }
        })
    }

    private fun setupSeatGrid() {
        binding.seatGrid.removeAllViews()
        val totalSeats = 32

        for (i in 1..totalSeats) {
            val seatName = "S$i"
            val textView = TextView(this)
            textView.text = seatName
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            
            val params = GridLayout.LayoutParams()
            params.width = 150
            params.height = 150
            params.setMargins(12, 12, 12, 12)
            textView.layoutParams = params
            
            if (bookedSeats.contains(seatName)) {
                textView.setBackgroundColor(Color.GRAY)
                textView.setTextColor(Color.WHITE)
                textView.isEnabled = false 
            } else {
                if (selectedSeats.contains(seatName)) {
                    textView.setBackgroundColor(Color.GREEN)
                    textView.setTextColor(Color.WHITE)
                } else {
                    textView.setBackgroundResource(android.R.drawable.btn_default)
                    textView.setTextColor(Color.BLACK)
                }

                textView.setOnClickListener {
                    if (selectedSeats.contains(seatName)) {
                        selectedSeats.remove(seatName)
                        textView.setBackgroundResource(android.R.drawable.btn_default)
                        textView.setTextColor(Color.BLACK)
                    } else {
                        if (selectedSeats.size >= 5) {
                            Toast.makeText(this, "Maximum 5 seats allowed", Toast.LENGTH_SHORT).show()
                        } else {
                            selectedSeats.add(seatName)
                            textView.setBackgroundColor(Color.GREEN)
                            textView.setTextColor(Color.WHITE)
                        }
                    }
                    updateSummary()
                }
            }
            binding.seatGrid.addView(textView)
        }
    }

    private fun updateSummary() {
        binding.tvSelectedSeats.text = "Selected: ${selectedSeats.joinToString(", ")}"
        binding.tvTotalPrice.text = "Total: ৳${selectedSeats.size * fare}"
    }
}
