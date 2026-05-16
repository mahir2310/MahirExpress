package com.example.mahirexpress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityAdminBookingsBinding
import com.example.mahirexpress.fragments.AdminBookingListFragment
import com.google.android.material.tabs.TabLayoutMediator
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminBookingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBookingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBookingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = BookingPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = arrayOf("All", "Confirmed", "Pending", "Cancelled")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private inner class BookingPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AdminBookingListFragment.newInstance("All")
                1 -> AdminBookingListFragment.newInstance("confirmed")
                2 -> AdminBookingListFragment.newInstance("pending")
                3 -> AdminBookingListFragment.newInstance("cancelled")
                else -> AdminBookingListFragment.newInstance("All")
            }
        }
    }
}
