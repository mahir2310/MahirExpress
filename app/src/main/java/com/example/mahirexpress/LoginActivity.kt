package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    checkUserRoleAndRedirect(uid)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRoleAndRedirect(uid: String) {
        FirebaseDatabase.getInstance().getReference("users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                binding.progressBar.visibility = View.GONE
                val role = snapshot.child("role").getValue(String::class.java)
                
                // Cache user info in SharedPreferences
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                
                val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("userId", uid)
                    putString("name", name)
                    putString("email", email)
                    putString("phone", phone)
                    putString("role", role)
                    apply()
                }

                if (role == "admin") {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else if (role == "manager") {
                    // Manager Dashboard can be same as Admin or separate
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finishAffinity()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
            }
    }
}
