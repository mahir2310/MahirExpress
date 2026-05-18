package com.example.mahirexpress.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.databinding.ItemBookingBinding
import com.example.mahirexpress.models.Booking

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onBookingClick: (Booking) -> Unit,
    private val onCancelClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        val binding = holder.binding
        
        binding.tvRouteInfo.text = "${booking.source} to ${booking.destination}"
        binding.tvDateInfo.text = "Date: ${booking.journeyDate}"
        binding.tvSeatsInfo.text = "Seats: ${booking.seats.joinToString(", ")}"
        binding.tvStatus.text = booking.status.replaceFirstChar { it.uppercase() }
        binding.tvAmount.text = "৳${booking.totalAmount}"
        
        // Show cancel button only if status is "confirmed"
        if (booking.status == "confirmed") {
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnCancel.setOnClickListener { onCancelClick(booking) }
        } else {
            binding.btnCancel.visibility = View.GONE
        }
        
        binding.root.setOnClickListener { onBookingClick(booking) }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        val diffCallback = BookingDiffCallback(bookings, newBookings)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        bookings = newBookings
        diffResult.dispatchUpdatesTo(this)
    }

    class BookingDiffCallback(private val oldList: List<Booking>, private val newList: List<Booking>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].bookingId == newList[newItemPosition].bookingId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
