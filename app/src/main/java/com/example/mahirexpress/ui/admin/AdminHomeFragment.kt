package com.example.mahirexpress.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.FragmentAdminHomeBinding
import com.example.mahirexpress.util.PreferenceManager
import com.example.mahirexpress.viewmodel.AdminViewModel

class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())
        
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_logout) {
                preferenceManager.clearSession()
                findNavController().navigate(R.id.action_global_to_login)
                true
            } else false
        }

        val adapter = BookingAdapter()
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = adapter

        viewModel.fetchAllBookings()

        viewModel.allBookings.observe(viewLifecycleOwner) { bookings ->
            adapter.submitList(bookings)
            binding.tvTotalBookings.text = bookings.size.toString()
        }

        viewModel.totalRevenue.observe(viewLifecycleOwner) { revenue ->
            binding.tvTotalRevenue.text = "$${String.format("%.2f", revenue)}"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
