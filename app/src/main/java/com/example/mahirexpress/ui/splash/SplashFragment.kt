package com.example.mahirexpress.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mahirexpress.R
import com.example.mahirexpress.databinding.FragmentSplashBinding
import com.example.mahirexpress.util.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferenceManager = PreferenceManager(requireContext())

        // Simple fade-in animation
        binding.logoContainer.alpha = 0f
        binding.logoContainer.animate().alpha(1f).setDuration(1500).start()

        lifecycleScope.launch {
            delay(2500) // Wait for animation and splash screen time

            if (preferenceManager.isLoggedIn()) {
                val role = preferenceManager.getUserData()["role"]
                when (role) {
                    "Admin" -> findNavController().navigate(R.id.action_login_to_customerHome) // Update with correct actions later
                    "Manager" -> findNavController().navigate(R.id.action_login_to_customerHome)
                    else -> findNavController().navigate(R.id.action_login_to_customerHome)
                }
            } else {
                findNavController().navigate(R.id.action_splash_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
