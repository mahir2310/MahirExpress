package com.example.mahirexpress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mahirexpress.databinding.ActivityMainBinding
import com.example.mahirexpress.fragments.BookingsFragment
import com.example.mahirexpress.fragments.HomeFragment
import com.example.mahirexpress.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val targetFragment = intent.getStringExtra("target_fragment")
        if (targetFragment == "bookings") {
            loadFragment(BookingsFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_bookings
        } else {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_bookings -> {
                    loadFragment(BookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
