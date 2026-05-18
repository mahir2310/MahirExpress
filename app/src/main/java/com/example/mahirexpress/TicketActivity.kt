package com.example.mahirexpress

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import com.example.mahirexpress.databinding.ActivityTicketBinding
import com.example.mahirexpress.utils.PrefManager
import java.io.OutputStream

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val route = intent.getStringExtra("route") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val seats = intent.getStringExtra("seats") ?: ""
        val bus = intent.getStringExtra("bus") ?: ""
        val amount = intent.getStringExtra("amount") ?: ""
        val bookingId = intent.getStringExtra("bookingId") ?: ""

        binding.tvTicketRoute.text = route
        binding.tvTicketDate.text = "Date: $date"
        binding.tvTicketSeats.text = "Seats: $seats"
        binding.tvTicketBus.text = "Bus: $bus"
        binding.tvTicketAmount.text = "Total Amount: $amount"
        binding.tvTicketId.text = "Booking ID: $bookingId"

        binding.btnDownload.setOnClickListener {
            saveTicketAsImage(binding.root)
        }
    }

    private fun saveTicketAsImage(view: View) {
        val bitmap = createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val filename = "Ticket_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MahirExpress")
            }
            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MahirExpress")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val image = java.io.File(imagesDir, filename)
            fos = java.io.FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Ticket saved to Pictures/MahirExpress", Toast.LENGTH_LONG).show()
        } ?: Toast.makeText(this, "Failed to save ticket", Toast.LENGTH_SHORT).show()
    }
}
