package com.example.mahirexpress.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mahirexpress.AdminDashboardActivity
import com.example.mahirexpress.EditProfileActivity
import com.example.mahirexpress.LoginActivity
import com.example.mahirexpress.SettingsActivity
import com.example.mahirexpress.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        loadProfile()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.btnAdminDashboard.setOnClickListener {
            startActivity(Intent(requireContext(), AdminDashboardActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            clearSharedPrefs()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadProfile() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        binding.tvName.text = sharedPref.getString("name", "User Name")
        binding.tvEmail.text = sharedPref.getString("email", "Email")
        binding.tvPhone.text = sharedPref.getString("phone", "Phone")

        val role = sharedPref.getString("role", "customer")
        if (role == "admin" || role == "manager") {
            binding.btnAdminDashboard.visibility = View.VISIBLE
        } else {
            binding.btnAdminDashboard.visibility = View.GONE
        }
    }

    private fun clearSharedPrefs() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
