package com.example.mahirexpress.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.FragmentRouteListBinding
import com.example.mahirexpress.viewmodel.RouteViewModel

class RouteListFragment : Fragment() {

    private var _binding: FragmentRouteListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RouteViewModel by viewModels()
    private val args: RouteListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = "${args.source} to ${args.destination}"
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        val adapter = RouteAdapter { routeId ->
            val action = RouteListFragmentDirections.actionRouteListToSeatSelection(routeId)
            findNavController().navigate(action)
        }

        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoutes.adapter = adapter

        viewModel.searchRoutes(args.source, args.destination, args.date)

        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            adapter.submitList(routes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.tvError.text = error
            binding.tvError.isVisible = error != null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
