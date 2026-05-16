package com.example.mahirexpress.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.RouteDetailActivity
import com.example.mahirexpress.adapters.RouteAdapter
import com.example.mahirexpress.databinding.FragmentHomeBinding
import com.example.mahirexpress.models.Route
import com.google.firebase.database.*
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RouteAdapter
    private val routeList = mutableListOf<Route>()
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().getReference("routes")

        adapter = RouteAdapter(routeList) { route ->
            val intent = Intent(requireContext(), RouteDetailActivity::class.java)
            intent.putExtra("routeId", route.routeId)
            intent.putExtra("fare", route.fare)
            intent.putExtra("source", route.source)
            intent.putExtra("destination", route.destination)
            intent.putExtra("busName", route.busName)
            intent.putExtra("departure", route.departureTime)
            intent.putExtra("arrival", route.arrivalTime)
            intent.putExtra("journeyDate", selectedDate)
            startActivity(intent)
        }

        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoutes.adapter = adapter

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.swipeRefresh.setOnRefreshListener {
            fetchRoutes()
        }

        fetchRoutes()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, yearSelected, monthOfYear, dayOfMonth ->
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, yearSelected)
            binding.etDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun fetchRoutes() {
        binding.progressBar.visibility = View.VISIBLE
        
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                routeList.clear()
                for (routeSnapshot in snapshot.children) {
                    val route = routeSnapshot.getValue(Route::class.java)
                    if (route != null) {
                        routeList.add(route)
                    }
                }
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                // Automatically display all routes on load
                adapter.updateData(routeList)
            }

            override fun onCancelled(error: DatabaseError) {
                if (_binding == null) return
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        })
    }

    private fun performSearch() {
        val source = binding.etSource.text.toString().trim()
        val destination = binding.etDestination.text.toString().trim()

        val filteredList = routeList.filter { route ->
            val matchSource = source.isEmpty() || route.source.contains(source, ignoreCase = true)
            val matchDest = destination.isEmpty() || route.destination.contains(destination, ignoreCase = true)
            
            matchSource && matchDest
        }

        adapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
