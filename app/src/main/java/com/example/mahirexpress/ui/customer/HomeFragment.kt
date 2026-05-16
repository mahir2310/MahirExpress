package com.example.mahirexpress.ui.customer

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.FragmentHomeBinding
import com.example.mahirexpress.util.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireContext())

        val userData = preferenceManager.getUserData()
        binding.tvWelcome.text = "Welcome, ${userData["name"] ?: "User"}!"

        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSearch.setOnClickListener {
            val source = binding.etSource.text.toString()
            val dest = binding.etDestination.text.toString()

            if (source.isNotBlank() && dest.isNotBlank() && selectedDate.isNotBlank()) {
                // Navigate to RouteList with arguments
                // val bundle = Bundle().apply {
                //     putString("source", source)
                //     putString("destination", dest)
                //     putString("date", selectedDate)
                // }
                // findNavController().navigate(R.id.action_home_to_routeList, bundle)
                Toast.makeText(context, "Searching for $source to $dest on $selectedDate", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            preferenceManager.clearSession()
            findNavController().navigate(R.id.action_login_to_customerHome) // Need to fix nav graph action for logout
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                calendar.set(year, month, dayOfMonth)
                selectedDate = sdf.format(calendar.time)
                binding.btnSelectDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
