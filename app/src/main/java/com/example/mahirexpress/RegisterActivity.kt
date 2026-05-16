package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityRegisterBinding
import com.example.mahirexpress.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val role = when (binding.rgRole.checkedRadioButtonId) {
            R.id.rbAdmin -> "admin"
            R.id.rbManager -> "manager"
            else -> "customer"
        }

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val user = User(userId, fullName, email, phone, role)
                    
                    database.getReference("users").child(userId).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            binding.progressBar.visibility = View.GONE
                            if (dbTask.isSuccessful) {
                                saveUserToSharedPrefs(user)
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                
                                val intent = if (role == "admin" || role == "manager") {
                                    Intent(this, AdminDashboardActivity::class.java)
                                } else {
                                    Intent(this, MainActivity::class.java)
                                }
                                startActivity(intent)
                                finishAffinity()
                            } else {
                                Toast.makeText(this, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToSharedPrefs(user: User) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("userId", user.userId)
            putString("name", user.name)
            putString("email", user.email)
            putString("phone", user.phone)
            putString("role", user.role)
            apply()
        }
    }
}
