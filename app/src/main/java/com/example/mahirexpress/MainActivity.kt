package com.example.mahirexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mahirexpress.ui.navigation.MahirNavGraph
import com.example.mahirexpress.ui.theme.MahirExpressTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MahirExpressTheme {
                MahirNavGraph()
            }
        }
    }
}
