package com.example.mahirexpress.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.databinding.ItemBusBinding
import com.example.mahirexpress.models.Bus

class BusAdapter(
    private var buses: List<Bus>,
    private val onBusClick: (Bus) -> Unit
) : RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    inner class BusViewHolder(val binding: ItemBusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val binding = ItemBusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = buses[position]
        val binding = holder.binding
        
        binding.tvBusName.text = bus.busName
        binding.tvRegNumber.text = "Reg: ${bus.registrationNumber}"
        binding.tvTotalSeats.text = "${bus.totalSeats} Seats"
        binding.tvLayout.text = "${bus.seatLayout} Layout"
        
        binding.root.setOnClickListener { onBusClick(bus) }
    }

    override fun getItemCount() = buses.size

    fun updateData(newBuses: List<Bus>) {
        val diffCallback = BusDiffCallback(buses, newBuses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        buses = newBuses
        diffResult.dispatchUpdatesTo(this)
    }

    class BusDiffCallback(private val oldList: List<Bus>, private val newList: List<Bus>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].busId == newList[newItemPosition].busId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
