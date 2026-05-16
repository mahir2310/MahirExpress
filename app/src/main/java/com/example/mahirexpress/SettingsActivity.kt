package com.example.mahirexpress

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mahirexpress.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val sharedPref = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // Load preferences
        binding.switchNotifications.isChecked = sharedPref.getBoolean("notifications", true)
        binding.switchDarkMode.isChecked = sharedPref.getBoolean("darkMode", false)

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notifications", isChecked).apply()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("darkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Language Spinner
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter
        
        binding.spinnerLanguage.setSelection(sharedPref.getInt("languagePos", 0))
        
        binding.tvTerms.setOnClickListener {
            startActivity(Intent(this, TermsActivity::class.java))
        }

        binding.tvAbout.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }
}
