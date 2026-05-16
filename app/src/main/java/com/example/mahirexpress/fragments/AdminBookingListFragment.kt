package com.example.mahirexpress.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.TicketActivity
import com.example.mahirexpress.adapters.BookingAdapter
import com.example.mahirexpress.databinding.FragmentBookingsBinding
import com.example.mahirexpress.models.Booking
import com.google.firebase.database.*

class AdminBookingListFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()
    private var statusFilter: String? = null

    companion object {
        fun newInstance(status: String) = AdminBookingListFragment().apply {
            arguments = Bundle().apply {
                putString("status_filter", status)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusFilter = arguments?.getString("status_filter")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("bookings")

        adapter = BookingAdapter(bookingList) { booking ->
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
        val query = if (statusFilter != null && statusFilter != "All") {
            database.orderByChild("status").equalTo(statusFilter)
        } else {
            database
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                bookingList.clear()
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    if (booking != null) {
                        bookingList.add(booking)
                    }
                }
                
                if (bookingList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvBookings.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvBookings.visibility = View.VISIBLE
                    adapter.updateData(bookingList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
