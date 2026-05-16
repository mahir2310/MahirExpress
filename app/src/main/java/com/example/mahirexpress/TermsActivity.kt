package com.example.mahirexpress

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.mahirexpress.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.webView.webViewClient = WebViewClient()
        // Loading a placeholder or a real URL
        binding.webView.loadUrl("https://www.google.com") // Replace with actual terms URL
        
        // Enable JS if needed
        binding.webView.settings.javaScriptEnabled = true
    }
}
