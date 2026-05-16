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
import com.example.mahirexpress.adapters.RouteAdapter
import com.example.mahirexpress.databinding.ActivityManageRoutesBinding
import com.example.mahirexpress.models.Route
import com.google.firebase.database.*

class ManageRoutesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageRoutesBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RouteAdapter
    private val routeList = mutableListOf<Route>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        database = FirebaseDatabase.getInstance().getReference("routes")

        // Initialize with empty list to force rendering on update
        adapter = RouteAdapter(emptyList()) { route ->
            // Logic for clicking a route item
        }
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val routeToDelete = routeList[position]
                    showDeleteConfirmation(routeToDelete, position)
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvRoutes)

        binding.fabAddRoute.setOnClickListener {
            startActivity(Intent(this, AddRouteActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            fetchRoutes()
        }

        fetchRoutes()
    }

    private fun showDeleteConfirmation(route: Route, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Route")
            .setMessage("Are you sure you want to delete this route from ${route.source} to ${route.destination}?")
            .setPositiveButton("Delete") { dialog: DialogInterface, _ ->
                database.child(route.routeId).removeValue().addOnCompleteListener { task ->
                    if (!isFinishing) {
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Route deleted", Toast.LENGTH_SHORT).show()
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

    private fun fetchRoutes() {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
        // Use single value event to fetch and render once
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (this@ManageRoutesActivity.isFinishing) return
                
                val newList = mutableListOf<Route>()
                for (routeSnapshot in snapshot.children) {
                    val route = routeSnapshot.getValue(Route::class.java)
                    if (route != null) {
                        newList.add(route)
                    }
                }
                
                routeList.clear()
                routeList.addAll(newList)
                
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                adapter.updateData(newList)
            }

            override fun onCancelled(error: DatabaseError) {
                if (this@ManageRoutesActivity.isFinishing) return
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }
}
