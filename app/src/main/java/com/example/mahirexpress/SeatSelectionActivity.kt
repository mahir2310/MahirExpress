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

        database = FirebaseDatabase.getInstance().getReference("seats").child(routeId ?: "default")

        fetchBookedSeats()

        binding.btnContinue.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
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
                    if (seatSnapshot.child("status").value == "booked") {
                        bookedSeats.add(seatSnapshot.key ?: "")
                    }
                }
                setupSeatGrid()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SeatSelectionActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSeatGrid() {
        binding.seatGrid.removeAllViews()
        val totalSeats = 40

        for (i in 1..totalSeats) {
            val seatName = "S$i"
            val textView = TextView(this)
            textView.text = seatName
            textView.textSize = 16f
            textView.gravity = Gravity.CENTER
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 120
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            textView.layoutParams = params
            
            if (bookedSeats.contains(seatName)) {
                textView.setBackgroundColor(Color.LTGRAY)
                textView.setTextColor(Color.WHITE)
                textView.isEnabled = false
            } else {
                textView.setBackgroundResource(android.R.drawable.btn_default)
                textView.setOnClickListener {
                    if (selectedSeats.contains(seatName)) {
                        selectedSeats.remove(seatName)
                        textView.setBackgroundResource(android.R.drawable.btn_default)
                    } else {
                        selectedSeats.add(seatName)
                        textView.setBackgroundColor(Color.GREEN)
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
