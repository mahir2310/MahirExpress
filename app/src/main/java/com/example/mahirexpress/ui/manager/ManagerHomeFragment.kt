package com.example.mahirexpress.ui.manager

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
import com.example.mahirexpress.databinding.FragmentManagerHomeBinding
import com.example.mahirexpress.ui.customer.RouteAdapter
import com.example.mahirexpress.util.PreferenceManager
import com.example.mahirexpress.viewmodel.ManagerViewModel

class ManagerHomeFragment : Fragment() {

    private var _binding: FragmentManagerHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ManagerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerHomeBinding.inflate(inflater, container, false)
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

        val adapter = RouteAdapter { /* Handle route edit if needed */ }
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoutes.adapter = adapter

        viewModel.fetchData()

        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            adapter.submitList(routes)
            binding.tvActiveRoutes.text = routes.size.toString()
        }

        viewModel.buses.observe(viewLifecycleOwner) { buses ->
            binding.tvTotalBuses.text = buses.size.toString()
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
