package com.example.mahirexpress

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mahirexpress.adapters.BusAdapter
import com.example.mahirexpress.databinding.ActivityManageBusesBinding
import com.example.mahirexpress.models.Bus
import com.google.firebase.database.*

class ManageBusesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageBusesBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: BusAdapter
    private val busList = mutableListOf<Bus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBusesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        database = FirebaseDatabase.getInstance().getReference("buses")

        adapter = BusAdapter(busList) { bus ->
            // Logic for clicking a bus item
        }
        binding.rvBuses.layoutManager = LinearLayoutManager(this)
        binding.rvBuses.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val busToDelete = busList[position]
                    showDeleteConfirmation(busToDelete, position)
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvBuses)

        binding.fabAddBus.setOnClickListener {
            startActivity(Intent(this, AddBusActivity::class.java))
        }

        fetchBuses()
    }

    private fun showDeleteConfirmation(bus: Bus, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Bus")
            .setMessage("Are you sure you want to delete ${bus.busName}?")
            .setPositiveButton("Delete") { dialog: DialogInterface, _ ->
                database.child(bus.busId).removeValue().addOnCompleteListener { task ->
                    if (!isFinishing) {
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Bus deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                            adapter.notifyItemChanged(position)
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }

    private fun fetchBuses() {
        binding.progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isFinishing) return
                busList.clear()
                for (busSnapshot in snapshot.children) {
                    val bus = busSnapshot.getValue(Bus::class.java)
                    if (bus != null) {
                        busList.add(bus)
                    }
                }
                binding.progressBar.visibility = View.GONE
                adapter.updateData(busList)
            }

            override fun onCancelled(error: DatabaseError) {
                if (isFinishing) return
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ManageBusesActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
