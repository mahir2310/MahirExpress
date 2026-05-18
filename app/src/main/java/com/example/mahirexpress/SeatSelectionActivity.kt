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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatSelectionBinding
    private lateinit var database: DatabaseReference
    private var routeId: String? = null
    private var fare: Double = 0.0
    private var source: String? = null
    private var destination: String? = null
    private var busId: String? = null
    private var totalSeats: Int = 40 // Set default to 40
    
    private val selectedSeats = mutableListOf<String>()
    private val bookedSeats = mutableSetOf<String>()
    private var totalAvailable = 0

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
        
        fetchBusCapacity()
        fetchRouteAvailableSeats()
        fetchBookedSeats()

        binding.btnContinue.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show()
            } else {
                checkExistingBookings(selectedSeats.size) { isAllowed ->
                    if (isAllowed) {
                        val intent = Intent(this, PassengerRegistrationActivity::class.java)
                        intent.putExtra("routeId", routeId)
                        intent.putExtra("fare", fare)
                        intent.putExtra("source", source)
                        intent.putExtra("destination", destination)
                        intent.putExtra("busId", busId)
                        intent.putStringArrayListExtra("selectedSeats", ArrayList(selectedSeats))
                        intent.putExtra("totalAmount", selectedSeats.size * fare)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Limit reached: You cannot book more than 5 seats total for this route.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun checkExistingBookings(currentSelectionCount: Int, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("bookings")
            .orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalBookedOnRoute = 0
                    for (booking in snapshot.children) {
                        if (booking.child("routeId").getValue(String::class.java) == routeId) {
                            val seats = booking.child("seats")
                            if (seats.exists()) {
                                totalBookedOnRoute += seats.children.count()
                            }
                        }
                    }
                    callback((totalBookedOnRoute + currentSelectionCount) <= 5)
                }
                override fun onCancelled(error: DatabaseError) { callback(false) }
            })
    }

    private fun fetchBusCapacity() {
        FirebaseDatabase.getInstance().getReference("buses").child(busId ?: "").child("totalSeats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalSeats = snapshot.getValue(Int::class.java) ?: 40
                    setupSeatGrid()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchRouteAvailableSeats() {
        FirebaseDatabase.getInstance().getReference("routes").child(routeId ?: "").child("availableSeats")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalAvailable = snapshot.getValue(Int::class.java) ?: 0
                }
                override fun onCancelled(error: DatabaseError) {}
            })
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

        for (i in 1..totalSeats) {
            val seatName = "S$i"
            val textView = TextView(this)
            textView.text = seatName
            textView.textSize = 20f
            textView.gravity = Gravity.CENTER
            
            val params = GridLayout.LayoutParams()
            params.width = 180
            params.height = 180
            params.setMargins(16, 16, 16, 16)
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
                            Toast.makeText(this, "You can select a maximum of 5 seats per booking.", Toast.LENGTH_SHORT).show()
                        } else if (selectedSeats.size >= totalAvailable) {
                            Toast.makeText(this, "No more seats available on this bus.", Toast.LENGTH_SHORT).show()
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
