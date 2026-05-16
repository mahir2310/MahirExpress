package com.example.mahirexpress.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.databinding.ItemPassengerFormBinding

class PassengerAdapter(private val selectedSeats: List<String>) :
    RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder>() {

    private val passengerData = mutableMapOf<String, Pair<String, String>>() // seat -> (name, age)

    inner class PassengerViewHolder(val binding: ItemPassengerFormBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val binding = ItemPassengerFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PassengerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        val seat = selectedSeats[position]
        holder.binding.tvSeatNumber.text = "Seat: $seat"

        // Remove existing text listeners to avoid duplicate entries when recycling
        holder.binding.etName.tag = null
        holder.binding.etAge.tag = null

        holder.binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val age = holder.binding.etAge.text.toString()
                passengerData[seat] = Pair(s.toString(), age)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        holder.binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = holder.binding.etName.text.toString()
                passengerData[seat] = Pair(name, s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun getItemCount() = selectedSeats.size

    fun getPassengerDetails(): Map<String, String> {
        val details = mutableMapOf<String, String>()
        passengerData.forEach { (seat, pair) ->
            details[seat] = "${pair.first} (${pair.second})"
        }
        return details
    }
}
