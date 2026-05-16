package com.example.mahirexpress.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.databinding.ItemRouteBinding
import com.example.mahirexpress.models.Route

class RouteAdapter(
    private var routes: List<Route>,
    private val onRouteClick: (Route) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(val binding: ItemRouteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        val binding = holder.binding
        
        binding.tvBusName.text = route.busName.ifEmpty { "Bus: ${route.busId}" }
        binding.tvRoute.text = "${route.source} to ${route.destination}"
        binding.tvTime.text = "${route.departureTime} - ${route.arrivalTime}"
        binding.tvFare.text = "৳${route.fare}"
        binding.tvSeats.text = "${route.availableSeats} Seats Left"
        binding.ratingBar.rating = route.rating
        
        binding.root.setOnClickListener { onRouteClick(route) }
    }

    override fun getItemCount() = routes.size

    fun updateData(newRoutes: List<Route>) {
        val diffCallback = RouteDiffCallback(routes, newRoutes)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        routes = newRoutes
        diffResult.dispatchUpdatesTo(this)
    }

    class RouteDiffCallback(private val oldList: List<Route>, private val newList: List<Route>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].routeId == newList[newItemPosition].routeId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
