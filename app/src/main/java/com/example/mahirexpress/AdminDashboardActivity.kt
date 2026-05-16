package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Route Management
        binding.cardAddRoute.setOnClickListener {
            startActivity(Intent(this, AddRouteActivity::class.java))
        }
        binding.cardManageRoutes.setOnClickListener {
            startActivity(Intent(this, ManageRoutesActivity::class.java))
        }

        // Bus Management
        binding.cardManageBuses.setOnClickListener {
            startActivity(Intent(this, ManageBusesActivity::class.java))
        }

        // Booking Reports (All system bookings)
        binding.cardReports.setOnClickListener {
            startActivity(Intent(this, AdminBookingsActivity::class.java))
        }

        // User Role Management
        binding.cardManageUsers.setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
