package com.example.mahirexpress.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.ItemBusRouteBinding
import com.example.mahirexpress.model.Route

class RouteAdapter(private val onRouteClick: (String) -> Unit) :
    ListAdapter<Route, RouteAdapter.RouteViewHolder>(RouteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemBusRouteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RouteViewHolder(private val binding: ItemBusRouteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(route: Route) {
            binding.tvBusName.text = "Mahir Express - ${route.routeId.take(5)}"
            binding.tvTime.text = "${route.departureTime} - ${route.arrivalTime}"
            binding.tvAvailableSeats.text = "Available Seats: ${route.availableSeats}"
            binding.tvFare.text = "$${route.fare}"

            val seatColor = if (route.availableSeats > 5) {
                R.color.seat_available
            } else {
                android.R.color.holo_red_dark
            }
            binding.tvAvailableSeats.setTextColor(ContextCompat.getColor(binding.root.context, seatColor))

            binding.root.setOnClickListener {
                onRouteClick(route.routeId)
            }
        }
    }

    class RouteDiffCallback : DiffUtil.ItemCallback<Route>() {
        override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem.routeId == newItem.routeId
        }

        override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
            return oldItem == newItem
        }
    }
}
