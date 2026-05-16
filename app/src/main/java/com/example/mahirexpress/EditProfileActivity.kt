package com.example.mahirexpress

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        loadCurrentData()

        binding.btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadCurrentData() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        binding.etName.setText(sharedPref.getString("name", ""))
        binding.etPhone.setText(sharedPref.getString("phone", ""))
    }

    private fun updateProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "phone" to phone
        )

        binding.progressBar.visibility = View.VISIBLE
        database.getReference("users").child(userId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (!isFinishing) {
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        saveToSharedPrefs(name, phone)
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun saveToSharedPrefs(name: String, phone: String) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("name", name)
            putString("phone", phone)
            apply()
        }
    }
}
