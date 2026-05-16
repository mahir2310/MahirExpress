package com.example.mahirexpress

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mahirexpress.databinding.ActivitySplashBinding
import com.example.mahirexpress.utils.PrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefManager = PrefManager(this)
        
        // Apply saved theme preference (Lab 11)
        if (prefManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                checkUserRoleAndRedirect(currentUser.uid)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2000)
    }

    private fun checkUserRoleAndRedirect(uid: String) {
        FirebaseDatabase.getInstance().getReference("users").child(uid).get()
            .addOnSuccessListener { snapshot ->
                val role = snapshot.child("role").getValue(String::class.java) ?: "customer"
                
                // Cache user info
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                
                prefManager.saveUser(uid, name, email, phone, role)

                if (role == "admin" || role == "manager") {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}
