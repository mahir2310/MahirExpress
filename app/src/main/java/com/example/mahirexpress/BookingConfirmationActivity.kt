package com.example.mahirexpress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityBookingConfirmationBinding

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        binding.btnViewTicket.setOnClickListener {
            // Ideally pass booking data here, but for now just go to My Bookings in MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("target_fragment", "bookings")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
