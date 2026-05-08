package com.example.mahirexpress.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mahirexpress.util.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: (String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val userData = preferenceManager.getUserData()
    val userName = userData["name"] ?: "User"

    var source by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("Select Date") }
    
    // Date Picker State
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        travelDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Column {
                Text(
                    text = "Welcome, $userName!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Where would you like to go today?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Card
        Card(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("From") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("To") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = travelDate)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { 
                        if (source.isNotBlank() && destination.isNotBlank() && travelDate != "Select Date") {
                            onSearchClick(source, destination, travelDate)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search Buses", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {
                preferenceManager.clearSession()
                onLogout()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
        ) {
            Text("Logout", color = MaterialTheme.colorScheme.error)
        }
    }
}
