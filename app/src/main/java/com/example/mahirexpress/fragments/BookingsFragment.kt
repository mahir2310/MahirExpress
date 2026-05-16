package com.example.mahirexpress.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.TicketActivity
import com.example.mahirexpress.adapters.BookingAdapter
import com.example.mahirexpress.databinding.FragmentBookingsBinding
import com.example.mahirexpress.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("bookings")

        // Initialize with empty list
        adapter = BookingAdapter(emptyList()) { booking ->
            val intent = Intent(requireContext(), TicketActivity::class.java).apply {
                putExtra("route", "${booking.source} to ${booking.destination}")
                putExtra("date", booking.journeyDate)
                putExtra("seats", booking.seats.joinToString(", "))
                putExtra("bus", booking.busName)
                putExtra("amount", "৳${booking.totalAmount}")
                putExtra("bookingId", booking.bookingId)
            }
            startActivity(intent)
        }

        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = adapter
        
        fetchBookings()
    }

    private fun fetchBookings() {
        val userId = auth.currentUser?.uid ?: return
        
        database.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (_binding == null) return
                    
                    val newList = mutableListOf<Booking>()
                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        if (booking != null) {
                            newList.add(booking)
                        }
                    }
                    
                    if (newList.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.rvBookings.visibility = View.GONE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                        binding.rvBookings.visibility = View.VISIBLE
                        adapter.updateData(newList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
